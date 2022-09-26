package cn.brk2outside.laser;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@SpringBootApplication
public class LaserCommApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LaserCommApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
