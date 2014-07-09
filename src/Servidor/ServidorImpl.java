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
                System.out.println("*"+apelidoOrigem+" enviou "+ mensagem +" Para: "+apelidoDestino+" "+ipCliente+":"+porta);
                ClienteInterface clienteInterface = (ClienteInterface) 
                        //Naming.lookup("rmi://" + ipCliente + ":" + porta + "/" + apelidoDestino);
                        Naming.lookup("rmi://localhost:" + porta + "/" + apelidoDestino);
                clienteInterface.ReceberMensagemServidor(apelidoOrigem, mensagem);
            } catch (NotBoundException ex) {
                Logger.getLogger(ServidorImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(ServidorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        return 0;
    }

    @Override
    public int Conectar(String apelido, String nome, String ipCliente, String portaCliente) throws RemoteException {
        try {
            //ClienteInterface clienteInterface = (ClienteInterface) Naming.lookup("rmi://" + ipCliente + ":" + portaCliente + "/" + apelido);
            ClienteInterface clienteInterface = (ClienteInterface) Naming.lookup("rmi://localhost:" + portaCliente + "/" + apelido);
            servidor.novoCliente(ipCliente, portaCliente, apelido, nome);
            clienteInterface.ReceberNovaConexao(apelido, nome);
        } catch (NotBoundException ex) {
            Logger.getLogger(ServidorImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServidorImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public void Desconectar(String apelido, String ipCliente, String portaCliente) throws RemoteException {
        servidor.desconectaCliente(apelido, ipCliente, portaCliente);
    }

}
