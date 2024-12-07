/**
 * Lab05: Sistema P2P
 * Daniel Costa e João Carvalho
 * Adicionado PEER4
 */

public enum PeerLista {
    
    PEER1 {
        @Override
        public String getNome() {
            return "PEER1";
        }        
    },
    PEER2 {
        public String getNome() {
            return "PEER2";
        }        
    },
    PEER3 {
        public String getNome() {
            return "PEER3";
        }        
    },
    PEER4 {
        public String getNome() {
            return "PEER4";
        }        
    };
    public String getNome(){
        return "NULO";
    }    
}
