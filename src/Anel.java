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
        for (int i = 0; i <= numero_processos; i++) {
            if (i != numero_processos - 1)
                listaProcessos.get(i).setProximo(listaProcessos.get(i + 1));
            else
                listaProcessos.get(i).setProximo(listaProcessos.get(0));
        }

        // INICIAR requisicao
        Processo start_req = listaProcessos.get(0);
        start_req.requisicao((int) start_req.getId());

    }

    public void eleicao(List<Integer> lista) {

        int aux = -1;
        int novo_coordenador = 0;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) > aux) {
                aux = (int) lista.get(i);
                novo_coordenador = i;
            }
        }
        gerenciador(novo_coordenador, "ELEITO");
    }

    public void sequenciaEleicao(int id, List<Integer> lista) {
        List<Integer> lista2 = lista;
        Processo processo = listaProcessos.get(id);
        Processo proximo;
        if (lista2.isEmpty()) {
            lista2.add((int) processo.getId());
        } else {
            if (processo.isAtivo()) {
                if (id == lista2.get(0)) {
                    eleicao(lista2);
                } else {
                    lista2.add(id);
                }
            } else {
                proximo = processo.getProximo();
                sequenciaEleicao((int) proximo.getId(), lista2);
            }
        }
    }

    public void gerenciador(int id_processo, String mensagem) {
        if (mensagem.equals("ELEICAO")) {
            System.out.println("O processo P" + id_processo + " solicitou uma eleicao");
            sequenciaEleicao(id_processo, new ArrayList<>());
        }

        if (mensagem.equals("ELEITO")) {
            System.out.println("O processo " + id_processo + " ganhou a eleicao");
            listaProcessos.get(id_processo).setCoordenador(true);
        }
    }

    public void finalizarCoordenador() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int coordId = -1;

                System.out.println("Verificando coordenador...");

                for (Processo processo : listaProcessos) {
                    coordId = processo.isCoordenador() ? (int)processo.getId() : -1;
                }

                if (coordId > 0) {
                    listaProcessos.get(coordId).setAtivo(false);
                    System.out.println("O processo coordenador parou");
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (coordId > 0) {
                    listaProcessos.get(coordId).setAtivo(true);
                    System.out.println("O processo P" + coordId + " voltou");
                    gerenciador(coordId, "ELEICAO");
                }
            }
        }).start();
    }
}