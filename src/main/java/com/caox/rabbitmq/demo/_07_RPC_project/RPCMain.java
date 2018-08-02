package com.caox.rabbitmq.demo._07_RPC_project;

/**
 * Created by nazi on 2018/7/26.
 */

public class RPCMain {

    public static void main(String[] args) throws Exception {
        RPCClient rpcClient = new RPCClient();
        System.out.println(" [x] Requesting getMd5String(abc)");
        String response = rpcClient.call("abc");
        System.out.println(" [.] Got '" + response + "'");
        rpcClient.close();
    }
}

