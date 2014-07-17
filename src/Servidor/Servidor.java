package Servidor;

import Interface.ServidorInterface;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import sun.rmi.registry.RegistryImpl;

public class Servidor {

    private DefaultTableModel tabelaClientesConectados;
    private DefaultTableModel tabelaLog;
    private HashMap<String, Contato> clientes;

    public Servidor(int porta) {
        
        clientes = new HashMap<String, Contato>();
        tabelaClientesConectados = new DefaultTableModel();
        tabelaClientesConectados.addColumn("Endereço");
        tabelaClientesConectados.addColumn("Nome");
        tabelaClientesConectados.addColumn("Apelido");
        tabelaClientesConectados.addColumn("Porta");
        
        tabelaLog = new DefaultTableModel();
        tabelaLog.addColumn("log");
        
        try {
            RegistryImpl impl = new RegistryImpl(porta); //carregar servidor de Registros        

            ServidorInterface servidorInterface = new ServidorImpl(this);
            
            String ipServ = InetAddress.getLocalHost().getHostAddress().toString();

            Naming.rebind("rmi://"+ipServ+":" + String.valueOf(porta) + "/servidorEco", servidorInterface);

            String msg = "> Servidor ONLINE!\n> aguardando chamadas de metodos...";
            
            atualizaTabelaLog(msg);
        } catch (RemoteException ex) {
            atualizaTabelaLog( "> Servidor de registro nao foi carregado");
        } catch (Exception e) {
            atualizaTabelaLog("> Problema do registro dos metodos");
        }
        
    }

    public DefaultTableModel getTabelaClientesConectados() {
        return tabelaClientesConectados;
    }
    
    public DefaultTableModel getTabelaLog() {
        return tabelaLog;
    }
    
    public void novoCliente(String enderecoOrigem, String portaOrigem, String apelido, String nome) {
        Contato novoCliente = new Contato(enderecoOrigem, portaOrigem, apelido, nome);
        clientes.put(novoCliente.getHash(), novoCliente);
        atualizaTabelaLog("> "+apelido+" Ip: "+enderecoOrigem+" Porta: "+portaOrigem+" - está conectado.");
        atualizaTabelaClientesConectadis();
    }

    private void atualizaTabelaClientesConectadis() {
        tabelaClientesConectados.setRowCount(0);

        for (Contato contato : clientes.values()) {
            String[] vetor = {contato.getEndereco(), contato.getNome(), contato.getApelido(), contato.getPorta()};
            tabelaClientesConectados.addRow(vetor);
        }
    }

    public String getPortaCliente(String apelidoDestino) {
        String porta = null;
        for (Contato contato : clientes.values()) {
            if (contato.getApelido().equals(apelidoDestino)) {
                porta = contato.getPorta();
            }
        }
        return porta;
    }

    public String getEnderecoCliente(String apelidoDestino) {
        String endereco = null;
        for (Contato contato : clientes.values()) {
            if (contato.getApelido().equals(apelidoDestino)) {
                endereco = contato.getEndereco();
            }
        }
        return endereco;
    }

    public String desconectaCliente(String apelidoOrigem, String ipCliente, String portaOrigem) {

        Contato contato = new Contato(ipCliente, portaOrigem);
        Contato c = clientes.remove(contato.getHash());
        atualizaTabelaLog("> "+c.getApelido()+" desconectou-se.");
        atualizaTabelaClientesConectadis();
        return c.getNome();
    }
    
    public HashMap<String, Contato> getListaClientes(){
        return clientes;
    }
    
    public void atualizaTabelaLog(String msg) {
        String[] vetor = {String.valueOf(msg)};
        tabelaLog.addRow(vetor);
    }
}
