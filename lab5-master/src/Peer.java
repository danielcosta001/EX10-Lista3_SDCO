/**
 * Lab05: Sistema P2P
 * Daniel Costa e João Carvalho
 *Adicionados metodo unbind, armazenamento do nome do peer comforme pedido pelo enunciado e Shutdownhook (pois tinha problemas para encerrar)
 *
 * 
 */


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Peer implements IMensagem {

    ArrayList<PeerLista> alocados;
    private String nomePeer;

    public Peer() {
        alocados = new ArrayList<>();
    }

    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta;
        try {
            System.out.println("Mensagem recebida: " + mensagem.getMensagem());
            resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
        } catch (Exception e) {
            e.printStackTrace();
            resposta = new Mensagem("{\n" + "\"result\": false\n" + "}");
        }
        return resposta;
    }

    public String parserJSON(String json) {
        String result = "false";
        String fortune = "-1";

        String[] v = json.split(":");
        System.out.println(">>>" + v[1]);
        String[] v1 = v[1].split("\"");
        System.out.println(">>>" + v1[1]);
        if (v1[1].equals("write")) {
            String[] p = json.split("\\[");
            System.out.println(p[1]);
            String[] p1 = p[1].split("]");
            System.out.println(p1[0]);
            String[] p2 = p1[0].split("\"");
            System.out.println(p2[1]);
            fortune = p2[1];

            // Write in file
            Principal pv2 = new Principal();
            pv2.write(fortune);
        } else if (v1[1].equals("read")) {
            // Read file
            Principal pv2 = new Principal();
            fortune = pv2.read();
        }

        result = "{\n" + "\"result\": \"" + fortune + "\"" + "}";
        System.out.println(result);

        return result;
    }

    public void iniciar() {
        try {
            List<PeerLista> listaPeers = new ArrayList<>();
            for (PeerLista peer : PeerLista.values()) {
                listaPeers.add(peer);
            }

            final Registry[] servidorRegistro = new Registry[1];

            try {
                servidorRegistro[0] = LocateRegistry.createRegistry(1099);
            } catch (java.rmi.server.ExportException e) {
                System.out.print("Registro já iniciado. Usar o ativo.\n");
            }
            servidorRegistro[0] = LocateRegistry.getRegistry();
            String[] listaAlocados = servidorRegistro[0].list();
            for (int i = 0; i < listaAlocados.length; i++)
                System.out.println(listaAlocados[i] + " ativo.");

            SecureRandom sr = new SecureRandom();
            PeerLista peer = listaPeers.get(sr.nextInt(listaPeers.size()));

            int tentativas = 0;
            boolean repetido = true;
            boolean cheio = false;
            while (repetido && !cheio) {
                repetido = false;
                peer = listaPeers.get(sr.nextInt(listaPeers.size()));
                for (int i = 0; i < listaAlocados.length && !repetido; i++) {
                    if (listaAlocados[i].equals(peer.getNome())) {
                        System.out.println(peer.getNome() + " ativo. Tentando próximo...");
                        repetido = true;
                        tentativas = i + 1;
                    }
                }

                if (listaAlocados.length > 0 &&
                        tentativas == listaPeers.size()) {
                    cheio = true;
                }
            }

            if (cheio) {
                System.out.println("Sistema cheio. Tente mais tarde.");
                System.exit(1);
            }

            IMensagem skeleton = (IMensagem) UnicastRemoteObject.exportObject(this, 0);
            servidorRegistro[0].rebind(peer.getNome(), skeleton);
            nomePeer = peer.getNome(); // Salva o nome do peer
            System.out.print(peer.getNome() + " Servidor RMI: Aguardando conexões...");

 
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    servidorRegistro[0].unbind(nomePeer);
                    System.out.println("\nPeer " + nomePeer + " desconectado.");
                } catch (Exception e) {
                    System.err.println("Erro ao realizar unbind: " + e.getMessage());
                }
            }));

            new ClienteRMI().iniciarCliente();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Peer servidor = new Peer();
        servidor.iniciar();
    }
}

