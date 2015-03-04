/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi;

import com.sun.jdi.connect.spi.Connection;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Matthew Tambara
 */
public class ConnectionWrapper extends Connection {

	public ConnectionWrapper(Connection connection) {
		_connection = connection;
	}

	public PipedInputStream connect() throws IOException {
		return new PipedInputStream(_pipedOutputStream);
	}

	@Override
	public void close() throws IOException {
		_connection.close();
	}

	@Override
	public boolean isOpen() {
		return _connection.isOpen();
	}

	@Override
	public byte[] readPacket() throws IOException {
		byte[] pkt = _connection.readPacket();

		if ((pkt[8] & _REPLY) == _REPLY) {
			for (byte[] commandId : _commandIds) {
				if (Arrays.equals(commandId, Arrays.copyOfRange(pkt, 4, 8))) {
					_pipedOutputStream.write(pkt, 0, pkt.length);
					_pipedOutputStream.flush();


					_commandIds.remove(commandId);

					return readPacket();
				}
			}
		}

		return pkt;

	}

	@Override
	public void writePacket(byte[] pkt) throws IOException {
		writePacket(pkt, false);
	}

	public void writePacket(byte[] pkt, boolean debugger) throws IOException {
		if (debugger && ((pkt[8] & _REPLY) != _REPLY)) {
			_commandIds.add(Arrays.copyOfRange(pkt, 4, 8));
		}

		_connection.writePacket(pkt);

	}

	private static final short _REPLY = 0x80;

	private final Connection _connection;
	private final Set<byte[]> _commandIds = Collections.newSetFromMap(
		new ConcurrentHashMap<byte[], Boolean>());
	private final PipedOutputStream _pipedOutputStream = new PipedOutputStream();

}
