import javax.swing.*;
import java.util.List;

public class Terminal {

    private Banco banco;

    public void run() {
        banco = new Banco("Mogi International Bank");
        Cliente atualCliente = null;
        Conta atualConta = null;

        JOptionPane.showMessageDialog(null, banco.getName(), "Bem-vindo", JOptionPane.INFORMATION_MESSAGE);

        while (true) {
            StringBuilder prompt = new StringBuilder("Menu - Mogi International Bank\n\n");
            prompt.append(atualCliente == null ? "Cliente: Nenhum\n" : "Cliente: " + atualCliente.getName() + "\n");
            prompt.append(atualConta == null ? "" : String.format("Conta: %s | Saldo: R$ %.2f\n", atualConta.getId(), atualConta.getSaldo()));
            prompt.append("\nEscolha uma opção:\n\n");
            prompt.append("""
                            1. Criar Cliente        
                            2. Listar Clientes
                            3. Selecionar Cliente   
                            4. Criar Conta
                            5. Listar Contas        
                            6. Selecionar Conta
                            7. Depositar            
                            8. Sacar
                            9. Listar Todas as Contas   
                            10. Render
                            11. Excluir Conta       
                            12. Excluir Cliente
                            13. Sair
                            """);

            String input = JOptionPane.showInputDialog(prompt.toString());
            if (input == null) continue;

            try {
                int escolha = Integer.parseInt(input);

                if (escolha == 13) {
                    JOptionPane.showMessageDialog(null, "Encerrando o programa. Até logo!");
                    break;
                }

                switch (escolha) {
                    case 1:
                        atualCliente = createCustomer();
                        banco.addCliente(atualCliente);
                        atualConta = null;
                        break;
                    case 2:
                        listCustomers();
                        break;
                    case 3:
                        List<Cliente> clientes = banco.getClientes();
                        if (clientes.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
                            break;
                        }
                        String[] clienteOpcoes = clientes.stream()
                                .map(c -> c.getName() + " [" + c.getId() + "]")
                                .toArray(String[]::new);
                        String escolhaCliente = (String) JOptionPane.showInputDialog(null,
                                "Selecione o cliente:", "Selecionar Cliente",
                                JOptionPane.PLAIN_MESSAGE, null, clienteOpcoes, clienteOpcoes[0]);
                        if (escolhaCliente != null) {
                            String idSelecionado = escolhaCliente.substring(escolhaCliente.indexOf("[") + 1, escolhaCliente.indexOf("]"));
                            atualCliente = banco.getCliente(idSelecionado);
                            atualConta = null;
                        }
                        break;
                    case 4:
                        if (atualCliente == null) throw new BancoException("D05", "Cliente não selecionado");
                        atualConta = createAccount(atualCliente);
                        atualCliente.addConta(atualConta);
                        banco.addConta(atualConta);
                        break;
                    case 5:
                        if (atualCliente == null) throw new BancoException("D05", "Cliente não selecionado");
                        listAccounts(atualCliente.getContas());
                        break;
                    case 6:
                        if (atualCliente == null) throw new BancoException("D05", "Cliente não selecionado");
                        List<Conta> contas = atualCliente.getContas();
                        if (contas.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Este cliente não possui contas.");
                            break;
                        }
                        String[] contaOpcoes = contas.stream()
                            .map(c -> {
                                String tipo = c instanceof ContaPoupanca ? "CP" :
                                            c instanceof ContaCorrente ? "CC" : "CI";
                                return tipo + " - " + c.toString() + " [" + c.getId() + "]";
                            })
                            .toArray(String[]::new);
                        String escolhaConta = (String) JOptionPane.showInputDialog(null,
                                "Selecione a conta:", "Selecionar Conta",
                                JOptionPane.PLAIN_MESSAGE, null, contaOpcoes, contaOpcoes[0]);
                        if (escolhaConta != null) {
                            String idConta = escolhaConta.substring(escolhaConta.indexOf("[") + 1, escolhaConta.indexOf("]"));
                            atualConta = atualCliente.getConta(idConta);
                        }
                        break;
                    case 7:
                        if (atualConta == null) throw new BancoException("D06", "Conta não selecionada");
                        double valorDeposito = inputValue("Valor para depósito:");
                        atualConta.depositar(valorDeposito);
                        break;
                    case 8:
                        if (atualConta == null) throw new BancoException("D06", "Conta não selecionada");
                        double valorSaque = inputValue("Valor para saque:");
                        atualConta.sacar(valorSaque);
                        break;
                    case 9:
                        listAccounts(banco.getContas());
                        break;
                    case 10:
                        banco.getContas().forEach(c -> {
                            if (c instanceof Rendimento) ((Rendimento) c).render();
                        });
                        JOptionPane.showMessageDialog(null, "Rendimento aplicado com sucesso.");
                        break;
                    case 11:
                        if (atualCliente == null) throw new BancoException("D05", "Cliente não selecionado");
                        List<Conta> contasRemocao = atualCliente.getContas();
                        if (contasRemocao.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Nenhuma conta para excluir.");
                            break;
                        }
                        String[] contasExcluir = contasRemocao.stream()
                            .map(c -> {
                                String tipo = c instanceof ContaPoupanca ? "Conta Poupança" :
                                            c instanceof ContaCorrente ? "Conta Corrente" :
                                            "Conta Investimento";
                                return tipo + " - " + c.toString() + " [" + c.getId() + "]";
                            })
                            .toArray(String[]::new);
                        String contaExcluir = (String) JOptionPane.showInputDialog(null,
                                "Escolha a conta para excluir:", "Excluir Conta",
                                JOptionPane.PLAIN_MESSAGE, null, contasExcluir, contasExcluir[0]);
                        if (contaExcluir != null) {
                            String idConta = contaExcluir.substring(contaExcluir.indexOf("[") + 1, contaExcluir.indexOf("]"));
                            banco.removeConta(idConta);
                            if (atualConta != null && atualConta.getId().equals(idConta)) atualConta = null;
                            JOptionPane.showMessageDialog(null, "Conta removida com sucesso.");
                        }
                        break;
                    case 12:
                        List<Cliente> clientesDel = banco.getClientes();
                        if (clientesDel.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Nenhum cliente para excluir.");
                            break;
                        }
                        String[] clienteExcluirOpcoes = clientesDel.stream()
                                .map(c -> c.getName() + " [" + c.getId() + "]")
                                .toArray(String[]::new);
                        String clienteExcluir = (String) JOptionPane.showInputDialog(null,
                                "Escolha o cliente para excluir:", "Excluir Cliente",
                                JOptionPane.PLAIN_MESSAGE, null, clienteExcluirOpcoes, clienteExcluirOpcoes[0]);
                        if (clienteExcluir != null) {
                            String idCliente = clienteExcluir.substring(clienteExcluir.indexOf("[") + 1, clienteExcluir.indexOf("]"));
                            banco.removeCliente(idCliente);
                            if (atualCliente != null && atualCliente.getId().equals(idCliente)) {
                                atualCliente = null;
                                atualConta = null;
                            }
                            JOptionPane.showMessageDialog(null, "Cliente removido com sucesso.");
                        }
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Opção inválida.");
                        break;
                }
            } catch (BancoException be) {
                JOptionPane.showMessageDialog(null, be.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listCustomers() {
        StringBuilder sb = new StringBuilder("Clientes:\n\n");
        banco.getClientes().forEach(c -> sb.append(c.toString()).append("\n"));
        JOptionPane.showMessageDialog(null, sb.toString(), "Clientes", JOptionPane.INFORMATION_MESSAGE);
    }

    private void listAccounts(List<Conta> contas) {
        StringBuilder sb = new StringBuilder("Contas:\n\n");
        for (Conta c : contas) {
            String tipo = c instanceof ContaPoupanca ? "CP" :
                          c instanceof ContaCorrente ? "CC" :
                          "CI";
            sb.append(tipo).append(" - ").append(c.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Contas", JOptionPane.INFORMATION_MESSAGE);
    }
    

    private Cliente createCustomer() {
        String nome = JOptionPane.showInputDialog("Nome do Cliente:");
        String tipo = JOptionPane.showInputDialog("Tipo: Física (f) ou Jurídica (j)?");

        if (tipo.equalsIgnoreCase("f")) {
            String cpf;
            do {
                cpf = JOptionPane.showInputDialog("CPF:");
                if (Util.isCpf(cpf)) break;
                JOptionPane.showMessageDialog(null, "CPF inválido.");
            } while (true);
            return new PessoaFisica(nome, cpf);
        } else {
            String cnpj = JOptionPane.showInputDialog("CNPJ:");
            return new PessoaJuridica(nome, cnpj);
        }
    }

    private Conta createAccount(Cliente cliente) {
        String tipo = JOptionPane.showInputDialog("Tipo da Conta: (P) Poupança | (C) Corrente | (I) Investimento").toLowerCase();
        switch (tipo) {
            case "p": return new ContaPoupanca(cliente);
            case "c": return new ContaCorrente(cliente);
            default:  return new ContaInvestimento(cliente);
        }
    }

    private double inputValue(String mensagem) {
        while (true) {
            try {
                return Double.parseDouble(JOptionPane.showInputDialog(mensagem));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Valor inválido.");
            }
        }
    }
}
