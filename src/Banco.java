import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Banco {

    private final String name;
    private final Map<String, Cliente> clientesById;
    private final Map<String, Conta> contasById;

    public Banco(String name) {
        this.name = name;
        this.clientesById = new HashMap<>();
        this.contasById = new HashMap<>();
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientesById.values());
    }

    public Cliente getCliente(String id) {
        return clientesById.get(id);
    }

    public void addCliente(Cliente cliente) {
        clientesById.put(cliente.getId(), cliente);
    }

    public void addConta(Conta conta) {
        contasById.put(conta.getId(), conta);
    }

    public List<Conta> getContas() {
        return new ArrayList<>(contasById.values());
    }

    public String getName() {
        return name;
    }
    public void removeConta(String contaIdExcluir) throws BancoException {
        Conta conta = contasById.get(contaIdExcluir);
        if (conta == null) {
            throw new BancoException("D08", "Conta não encontrada!");
        }

        Cliente cliente = conta.getCliente();
        if (cliente != null) {
            cliente.removeConta(conta.getId());
        }

        contasById.remove(contaIdExcluir);
    }
    
    public void removeCliente(String clienteIdExcluir) throws BancoException {
        Cliente cliente = clientesById.get(clienteIdExcluir);
        if (cliente == null) {
            throw new BancoException("D07", "Cliente não encontrado!");
        }

        for (Conta conta : cliente.getContas()) {
            contasById.remove(conta.getId());
        }

        clientesById.remove(clienteIdExcluir);
    }
}

