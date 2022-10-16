import java.util.ArrayList;
import java.util.List;

public class Anel {

    private static Anel anel;
    private int numero_processos = 6;

    public static synchronized Anel getInstance() {
        if (anel == null) {
            return anel = new Anel();
        } else {
            return anel;
        }
    }

    public List<Processo> listaProcessos = new ArrayList<Processo>();
    private final Object lock = new Object();

    public void gerarProcesso() {
        for (int i = 0; i < numero_processos; i++) {
            listaProcessos.add(new Processo(i, false, true));
            System.out.println("Processo P" + i + " criado com sucesso");
        }
        for (int i = 0; i < numero_processos; i++) {
            if (i != numero_processos - 1)
                listaProcessos.get(i).setProximo(listaProcessos.get(i + 1));
            else
                listaProcessos.get(i).setProximo(listaProcessos.get(0));
        }
    }

    public void eleicao(int id) {
        System.out.println("O processo P" + id + "solicitou uma eleicao");
        int aux = -1;
        int novo_coordenador = 0;

        for (int i = 0; i < listaProcessos.size(); i++) {
            if (listaProcessos.get(i).getId() > aux) {
                aux = (int) listaProcessos.get(i).getId();
                novo_coordenador = i;
            }
        }
        listaProcessos.get(novo_coordenador).setCoordenador(true);

        System.out.println(
                "O processo " + listaProcessos.get(novo_coordenador).getId() + " ganhou a eleicao");
    }

    public void finalizarCoordenador(int id) {
        listaProcessos.get(id).setAtivo(false);
        System.out.println("O processo coordenador parou");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listaProcessos.get(id).setAtivo(true);
        System.out.println("O processo P" + id + " voltou");
        eleicao(id);
    }
}