package sr.grpc.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

public class grpcServer 
{
	private static final Logger logger = Logger.getLogger(grpcServer.class.getName());

	private final String address = "127.0.0.5";
	private final int port = 50051;
	private Server server;

	private SocketAddress socket;

	private void start() throws IOException 
	{
		try { socket = new InetSocketAddress(InetAddress.getByName(address), port);	} catch(UnknownHostException e) {};

		server = NettyServerBuilder.forAddress(socket).executor(Executors.newFixedThreadPool(16))
				.addService(ProtoReflectionService.newInstance())
				.addService(new CalculatorImpl())
				.addService(new StreamTesterImpl())
				.build()
				.start();
		logger.info("Server started, listening on " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("Shutting down gRPC server...");
				grpcServer.this.stop();
				System.err.println("Server shut down.");
			}
		});
	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon threads.
	 */
	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	/**
	 * Main launches the server from the command line.
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		final grpcServer server = new grpcServer();
		server.start();
		server.blockUntilShutdown();
	}

}
