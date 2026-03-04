package ru.otus.hw;

import java.io.InputStream;
import java.util.Scanner;

public class StreamsInputService implements InputService {
    private final Scanner scanner;

    public StreamsInputService(InputStream inputStream) {
        this.scanner = new Scanner(inputStream);
    }

    @Override
    public String readString() {
        return scanner.nextLine();
    }

    @Override
    public int readInt() {
        return scanner.nextInt();
    }
}
