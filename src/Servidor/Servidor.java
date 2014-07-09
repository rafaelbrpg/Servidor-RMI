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
    private HashMap<String, Contato> clientes;

    public Servidor(int porta) {

        try {
            RegistryImpl impl = new RegistryImpl(porta); //carregar servidor de Registros        

            ServidorInterface servidorInterface = new ServidorImpl(this);

            Naming.rebind("rmi://localhost:" + String.valueOf(porta) + "/servidorEco", servidorInterface);

            System.out.println("Servidor aguardando chamadas de metodos");
        } catch (RemoteException ex) {
            System.out.println("Servidor de registro nao foi carregado");
        } catch (Exception e) {
            System.out.println("Problema do registro dos metodos");
        }
        clientes = new HashMap<String, Contato>();
        tabelaClientesConectados = new DefaultTableModel();
        tabelaClientesConectados.addColumn("Endereço");
        tabelaClientesConectados.addColumn("Nome");
        tabelaClientesConectados.addColumn("Apelido");
        tabelaClientesConectados.addColumn("Porta");
    }

    public DefaultTableModel getTabelaClientesConectados() {
        return tabelaClientesConectados;
    }

    public void novoCliente(String enderecoOrigem, String portaOrigem, String apelido, String nome) {
        Contato novoCliente = new Contato(enderecoOrigem, portaOrigem, apelido, nome);
        clientes.put(novoCliente.getHash(), novoCliente);
        //enviarListaClientes();
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

    public void desconectaCliente(String apelidoOrigem, String ipCliente, String portaOrigem) {

        Contato contato = new Contato(ipCliente, portaOrigem);
        Contato c = clientes.remove(contato.getHash());
        System.out.println(c + " Desconectou");
        //enviarListaClientes();
        atualizaTabelaClientesConectadis();
    }
}
