package Servidor;

import Interface.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorImpl extends UnicastRemoteObject implements ServidorInterface {

    Servidor servidor;

    public ServidorImpl(Servidor serv) throws RemoteException {
        servidor = serv;
    }

    //método remoto definido na interface
    @Override
    public int ReceberMensagemCliente(String apelidoOrigem, String apelidoDestino, String mensagem) throws RemoteException {

        try {
            String porta = servidor.getPortaCliente(apelidoDestino);
            String ipCliente = servidor.getEnderecoCliente(apelidoDestino);
            System.out.println("*" + apelidoOrigem + " enviou " + mensagem + " Para: " + apelidoDestino + " " + ipCliente + ":" + porta);
            ClienteInterface clienteInterface = (ClienteInterface) //Naming.lookup("rmi://" + ipCliente + ":" + porta + "/" + apelidoDestino);
                    Naming.lookup("rmi://localhost:" + porta + "/" + apelidoDestino);
            clienteInterface.ReceberMensagemServidor(apelidoOrigem, mensagem);
            
            servidor.atualizaTabelaLog("> "+apelidoOrigem +" escreveu para "+apelidoDestino+".");
        } catch (NotBoundException ex) {
            Logger.getLogger(ServidorImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServidorImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public int Conectar(String apelido, String nome, String ipCliente, String portaCliente) throws RemoteException {
        //ClienteInterface clienteInterface = (ClienteInterface) Naming.lookup("rmi://" + ipCliente + ":" + portaCliente + "/" + apelido);
        servidor.novoCliente(ipCliente, portaCliente, apelido, nome);

        for (Contato destino : servidor.getListaClientes().values()) {
            try {
                //ClienteInterface clienteInterface = (ClienteInterface) Naming.lookup("rmi://"+ destino.getEndereco+":"+ destino.getPorta() + "/" + destino.getApelido());
                ClienteInterface clienteInterface = (ClienteInterface) Naming.lookup("rmi://localhost:" + destino.getPorta() + "/" + destino.getApelido());
                for (Contato cliente : servidor.getListaClientes().values()) {
                    if (!cliente.equals(destino)) {
                        clienteInterface.ReceberNovaConexao(cliente.getApelido(), cliente.getNome());
                    }
                }
            } catch (NotBoundException ex) {
                System.out.println("Erro conexão 1");
            } catch (MalformedURLException ex) {
                System.out.println("Erro conexão 2");
            }
        }
        System.out.println("Conectou.");
        return 0;
    }

    @Override
    public void Desconectar(String apelido, String ipCliente, String portaCliente) throws RemoteException {
        String nome = servidor.desconectaCliente(apelido, ipCliente, portaCliente);

        for (Contato destino : servidor.getListaClientes().values()) {
            try {
                //ClienteInterface clienteInterface = (ClienteInterface) Naming.lookup("rmi://"+ destino.getEndereco+":"+ destino.getPorta() + "/" + destino.getApelido());
                ClienteInterface clienteInterface = (ClienteInterface) Naming.lookup("rmi://localhost:" + destino.getPorta() + "/" + destino.getApelido());
                clienteInterface.DesconexaoCliente(apelido, nome);
            } catch (NotBoundException ex) {
                System.out.println("Erro conexão 1");
            } catch (MalformedURLException ex) {
                System.out.println("Erro conexão 2");
            }
        }
        
    }

}
