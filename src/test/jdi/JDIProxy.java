/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi;

import com.sun.jdi.connect.spi.ClosedConnectionException;
import com.sun.jdi.connect.spi.Connection;
import com.sun.tools.jdi.SocketTransportService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.net.Socket;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Matthew Tambara
 */
public class JDIProxy {

	public JDIProxy(Connection connection, Socket socket) {
		_connection = connection;
		_socket = socket;
	}

	public Connection init() {
		_connectionWrapper = new ConnectionWrapper(_connection);
		
		return _connectionWrapper;
	}

	public void start() throws Exception {
		final OutputStream toDebugger = _socket.getOutputStream();
		final InputStream fromDebugger = _socket.getInputStream();

		final boolean easyRead = false;

		final File read = new File("FromJVM");
		final File write = new File("ToJVM");

		if (easyRead) {
			if (read.exists()) {
				read.delete();
			}

			if (write.exists()) {
				write.delete();
			}

			if (read.createNewFile() && write.createNewFile()) {
				System.out.println("Files made");
			}
		}

		SocketTransportService socketTransportService =
			new SocketTransportService();

		Class cls = socketTransportService.getClass();

		Method method = cls.getDeclaredMethod(
			"handshake", Socket.class, long.class);

		method.setAccessible(true);
		method.invoke(socketTransportService, _socket, 0);

		System.out.println("Handshake good");

		Thread input = new Thread() {

			@Override
			public void run() {
				byte[] toJVM = new byte[1024];
// try using a pushbackinputstream or something (maybe mark() and reset()?)
				try {
					int bytesRead;

					while ((bytesRead = fromDebugger.read(toJVM)) != -1) {
						toJVM = Arrays.copyOfRange(toJVM, 0, bytesRead);

						StringBuilder sb = new StringBuilder();
						if (easyRead) {
							printPacket("Writing to JVM", toJVM, write);
						}

						_connectionWrapper.writePacket(toJVM, true);

						toJVM = new byte[1024];
					}

				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		};

		Thread output = new Thread() {

			@Override
			public void run() {
				int b1, b2, b3, b4;
			
				try {
					PipedInputStream pipedInputStream =
						_connectionWrapper.connect();

					while (true) {
						b1 = pipedInputStream.read();					
						b2 = pipedInputStream.read();					
						b3 = pipedInputStream.read();
						b4 = pipedInputStream.read();
						
						int len = ((b1 << 24) | (b2 << 16) | (b3 << 8) | b4);

						byte fromJVM[] = new byte[len];

						fromJVM[0] = (byte)b1;
						fromJVM[1] = (byte)b2;
						fromJVM[2] = (byte)b3;
						fromJVM[3] = (byte)b4;
						
						int index = 4;
						len -= index;

						while (len > 0) {
							int count;

							count = pipedInputStream.read(fromJVM, index, len);
							
							len -= count;
							index += count;
						}

						if (easyRead) {
							printPacket("Reading from JVM", fromJVM, read);
						}

						toDebugger.write(fromJVM, 0, fromJVM.length);
//							clientOut.flush();
					}
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		};

		output.start();

		input.start();

	}

	public static Connection createProxyConnection(Connection connection) {
		return (Connection)SocketInvocationHandler.createProxy(connection, _commandIds);
	}

	private static void printPacket(String header, byte[] packet, File file) {
		byte[] size, id, flag, commandSet, command, data, errorCode;

		size = Arrays.copyOfRange(packet, 0, 4);

		id = Arrays.copyOfRange(packet, 4, 8);

		flag = Arrays.copyOfRange(packet, 8, 9);

		StringBuilder sb = new StringBuilder();

		sb.append("\n" + header);
		sb.append("\n\tSize: " + Arrays.toString(size));
		sb.append("\n\tId: " + Arrays.toString(id));
		sb.append("\n\tflag: " + Arrays.toString(flag));

		if (flag[0] == -128) {
			errorCode = Arrays.copyOfRange(packet, 9, 11);

			sb.append("\n\tError code: " + Arrays.toString(errorCode));
		}
		else {
			commandSet = Arrays.copyOfRange(packet, 9, 10);

			command = Arrays.copyOfRange(packet, 10, 11);

			sb.append("\n\tCommand set: " + Arrays.toString(commandSet));
			sb.append("\n\tCommand: " + Arrays.toString(command));
		}

		data = Arrays.copyOfRange(packet, 11, packet.length);

		sb.append("\n\tData: " + Arrays.toString(data));

		String out = sb.toString();

		try (FileWriter fileWriter = new FileWriter(file, true)) {
			fileWriter.write(out);
			fileWriter.flush();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		System.out.println(out);
	}

	private Connection _connection;
	private ConnectionWrapper _connectionWrapper;
	private final Socket _socket;
	private static final Set<Byte[]> _commandIds = new HashSet<>();

}

class SocketInvocationHandler implements InvocationHandler {
	public static Object createProxy(Object connection, Set commandIds) {
		return Proxy.newProxyInstance(
			connection.getClass().getClassLoader(), 
			new Class<?>[]{connection.getClass().getSuperclass()},
			new SocketInvocationHandler(connection, commandIds));
	}

	private Object _target;
	private Set<Byte[]> _commandIds;

	private SocketInvocationHandler(Object obj, Set commandIds) {
		_commandIds = commandIds;
		_target = obj;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		Object result = null;

		try {
			if (method.getName().equals("readPacket")) {

			}
			else {
				result = method.invoke( _target, args );
			}
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		System.out.println( method.getName() + " returns" );

		return result;
	}
}
