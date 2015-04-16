package com.github.redis.server.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketTest
{
    public static void main(String[] args) throws IOException
    {
        final String command = "*3\r\n$3\r\nSET\r\n$1\r\na\r\n$1\r\nb\r\n";
        for (int i = 0; i < 10; i++)
        {
            new Thread()
            {
                public void run()
                {
                    Socket socket;
                    try
                    {
                        socket = new Socket("localhost", 6379);
                        InputStream in = socket.getInputStream();
                        OutputStream out = socket.getOutputStream();
                        byte[] b = new byte[10];
                        for (int i = 0; i < 1; i++)
                        {
                            out.write(command.getBytes());
                            in.read(b);
                            output(b);
                        }
                        socket.close();
                    } catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                };
            }.start();
        }
    }

    private static void output(byte[] b)
    {
        for (byte c : b)
        {
            System.out.print(c);
        }
        System.out.println("====="+Thread.currentThread().getName());

    }
}
