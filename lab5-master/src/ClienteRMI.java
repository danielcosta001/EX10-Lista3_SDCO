/**
 * Lab05: Sistema P2P
 * 
 * Daniel Costa e João Carvalho
 * 
 * Adicionado imports swing para fazer interface gráfica
 * Adicionado método criarInterface
 * Adicionado menu para seleção do PEER que deseja conectar sugestão 3
 * OBS: a interface grafica dos peers ativos é iniciada apenas após escolha bem sucedida do peer
 */

 import javax.swing.*;
 import javax.swing.table.DefaultTableModel;
 import java.rmi.registry.LocateRegistry;
 import java.rmi.registry.Registry;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Scanner;
 
 public class ClienteRMI {
 
	 public void iniciarCliente() {
		 List<PeerLista> listaPeers = new ArrayList<>();
		 for (PeerLista peer : PeerLista.values()) {
			 listaPeers.add(peer);
		 }
 
		 try {
			 Registry registro = LocateRegistry.getRegistry("127.0.0.1", 1099);
 
			 IMensagem stub = null;
			 PeerLista peer = null;
 
			 boolean conectou = false;
			 while (!conectou) {
				 // Apresenta menu para seleção de peer
				 peer = selecionarPeer(listaPeers);
				 try {
					 stub = (IMensagem) registro.lookup(peer.getNome());
					 conectou = true;
				 } catch (java.rmi.ConnectException e) {
					 System.out.println("\n" + peer.getNome() + " indisponível. ConnectException. Tente novamente.");
				 } catch (java.rmi.NotBoundException e) {
					 System.out.println("\n" + peer.getNome() + " indisponível. NotBoundException. Tente novamente.");
				 }
			 }
			 System.out.println("Conectado no peer: " + peer.getNome());
 
			 // Cria e exibe a interface gráfica após a conexão
			 SwingUtilities.invokeLater(() -> criarInterface(listaPeers));
 
			 String opcao = "";
			 Scanner leitura = new Scanner(System.in);
			 do {
				 System.out.println("1) Read");
				 System.out.println("2) Write");
				 System.out.println("x) Exit");
				 System.out.print(">> ");
				 opcao = leitura.next();
				 switch (opcao) {
					 case "1": {
						 Mensagem mensagem = new Mensagem("", opcao);
						 Mensagem resposta = stub.enviar(mensagem);
						 System.out.println(resposta.getMensagem());
						 break;
					 }
					 case "2": {
						 System.out.print("Add fortune: ");
						 String fortune = leitura.next();
 
						 Mensagem mensagem = new Mensagem(fortune, opcao);
						 Mensagem resposta = stub.enviar(mensagem);
						 System.out.println(resposta.getMensagem());
						 break;
					 }
				 }
			 } while (!opcao.equals("x"));
 
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }
 
	 private PeerLista selecionarPeer(List<PeerLista> listaPeers) {
		 Scanner scanner = new Scanner(System.in);
		 PeerLista peerSelecionado = null;
 
		 while (peerSelecionado == null) {
			 System.out.println("\nSelecione um Peer:");
			 for (int i = 0; i < listaPeers.size(); i++) {
				 System.out.println((i + 1) + ") " + listaPeers.get(i).getNome());
			 }
			 System.out.print(">> ");
			 try {
				 int escolha = Integer.parseInt(scanner.nextLine());
				 if (escolha > 0 && escolha <= listaPeers.size()) {
					 peerSelecionado = listaPeers.get(escolha - 1);
				 } else {
					 System.out.println("Opção inválida. Tente novamente.");
				 }
			 } catch (NumberFormatException e) {
				 System.out.println("Entrada inválida. Digite um número.");
			 }
		 }
		 return peerSelecionado;
	 }
 
	 private void criarInterface(List<PeerLista> listaPeers) {
		 JFrame frame = new JFrame("Lista de Peers Ativos");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 DefaultTableModel modeloTabela = new DefaultTableModel(new Object[]{"Peers Ativos"}, 0);
 
		 for (PeerLista peer : listaPeers) {
			 modeloTabela.addRow(new Object[]{peer.getNome()});
		 }
 
		 JTable tabela = new JTable(modeloTabela);
		 tabela.setEnabled(false);
 
		 JScrollPane scrollPane = new JScrollPane(tabela);
		 frame.add(scrollPane);
 
		 frame.setSize(300, 200);
		 frame.setLocationRelativeTo(null);
		 frame.setVisible(true);
	 }

    /*public static void main(String[] args) {
                
    	new ClienteRMI().iniciarCliente();
    	        
    }*/
    
}
