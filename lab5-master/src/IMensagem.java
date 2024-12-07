/**
 * Lab05: Sistema P2P
 * 
 * Daniel Costa e João Carvalho
 */

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IMensagem extends Remote {
    
    public Mensagem enviar(Mensagem mensagem) throws RemoteException;
    
}
