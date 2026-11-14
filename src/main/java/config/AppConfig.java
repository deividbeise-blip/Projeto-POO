package config;

import aplicacao.dto.DadosCartao;
import aplicacao.dto.DadosDinheiro;
import aplicacao.dto.DadosPagamento;
import aplicacao.dto.DadosPix;
import aplicacao.pagamento.FormaPagamento;
import aplicacao.pagamento.FormaPagamentoCartao;
import aplicacao.pagamento.FormaPagamentoDinheiro;
import aplicacao.pagamento.FormaPagamentoPix;
import aplicacao.servico.ClienteService;
import aplicacao.servico.ConcessionariaService;
import aplicacao.servico.VeiculoService;
import aplicacao.servico.VendaService;
import aplicacao.servico.VendedorService;
import dominio.politica.DescontoPix;
import dominio.politica.SemDesconto;
import dominio.repositorio.ClienteRepository;
import dominio.repositorio.ConcessionariaRepository;
import dominio.repositorio.PagamentoRepository;
import dominio.repositorio.VeiculoRepository;
import dominio.repositorio.VendaRepository;
import dominio.repositorio.VendedorRepository;
import infra.conexao.ConexaoBanco;
import infra.conexao.TransacaoJdbc;
import infra.dao.ClienteDAO;
import infra.dao.ConcessionariaDAO;
import infra.dao.PagamentoCartaoDAO;
import infra.dao.PagamentoDAO;
import infra.dao.PagamentoPixDAO;
import infra.dao.VeiculoDAO;
import infra.dao.VendaDAO;
import infra.dao.VendedorDAO;

import java.util.Map;

/**
 * Único lugar do sistema que conhece as implementações concretas (DAOs).
 * Monta os services com as dependências certas e entrega tudo pronto para as telas.
 */
public class AppConfig {

    // Uma única fábrica de conexões, que também controla a transação: todos os DAOs a recebem.
    private final TransacaoJdbc transacao = new TransacaoJdbc(new ConexaoBanco());

    private final ClienteRepository clienteRepository = new ClienteDAO(transacao);
    private final ConcessionariaRepository concessionariaRepository = new ConcessionariaDAO(transacao);
    private final VeiculoRepository veiculoRepository = new VeiculoDAO(transacao);
    private final VendedorRepository vendedorRepository = new VendedorDAO(transacao);
    private final VendaRepository vendaRepository = new VendaDAO(transacao);
    private final PagamentoRepository pagamentoRepository = new PagamentoDAO(transacao);

    // Formas de pagamento aceitas: para uma nova, basta registrar mais uma linha aqui.
    private final Map<Class<? extends DadosPagamento>, FormaPagamento<?, ?>> formasPagamento = Map.of(
            DadosPix.class, new FormaPagamentoPix(new PagamentoPixDAO(transacao), new DescontoPix()),
            DadosCartao.class, new FormaPagamentoCartao(new PagamentoCartaoDAO(transacao), new SemDesconto()),
            DadosDinheiro.class, new FormaPagamentoDinheiro(new SemDesconto()));

    private final ClienteService clienteService = new ClienteService(clienteRepository);
    private final ConcessionariaService concessionariaService = new ConcessionariaService(concessionariaRepository);
    private final VendedorService vendedorService = new VendedorService(vendedorRepository, concessionariaRepository);
    private final VeiculoService veiculoService = new VeiculoService(veiculoRepository, concessionariaRepository);
    private final VendaService vendaService = new VendaService(
            clienteRepository,
            vendedorRepository,
            veiculoRepository,
            vendaRepository,
            pagamentoRepository,
            formasPagamento,
            transacao);

    /** @return casos de uso de clientes */
    public ClienteService clienteService() { return clienteService; }
    /** @return casos de uso de concessionárias */
    public ConcessionariaService concessionariaService() { return concessionariaService; }
    /** @return casos de uso de vendedores */
    public VendedorService vendedorService() { return vendedorService; }
    /** @return casos de uso de veículos */
    public VeiculoService veiculoService() { return veiculoService; }
    /** @return casos de uso de vendas */
    public VendaService vendaService() { return vendaService; }
}
