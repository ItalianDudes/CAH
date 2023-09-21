LOGIN:
    - Il client manda il protocollo di Login Main e il suo nome utente.
    - Il server riceve lo username e controlla che non esista già un utente con quel nome, se esiste rifiuta la connessione mandando "0", altrimenti accetta e manda "1".
    - Il client manda il protocollo di Login Service e il suo nome utente.
    - Il server riceve lo username e controlla che esista sul main quel nome utente, se esiste accetta e manda "1", altrimenti manda "0".
    - In caso qualcosa vada storto, il server manda "-1".

LOBBY:
    - Client:
        - Ready / Not Ready
        - List
    - Server:
        - Timer Init
        - Timer Interrupt
        - Start

MASTER CHOOSER:
    - Server:
        - Master / Player