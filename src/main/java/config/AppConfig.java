package config;

import aplicacao.servico.ClienteService;
import aplicacao.servico.VeiculoService;
import aplicacao.servico.VendaService;
import dominio.repositorio.ClienteRepository;
import dominio.repositorio.ConcessionariaRepository;
import dominio.repositorio.PagamentoRepository;
import dominio.repositorio.VeiculoRepository;
import dominio.repositorio.VendaRepository;
import dominio.repositorio.VendedorRepository;
import aplicacao.Transacao;
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

    private final ClienteService clienteService = new ClienteService(clienteRepository);
    private final VeiculoService veiculoService = new VeiculoService(veiculoRepository, concessionariaRepository);
    private final VendaService vendaService = new VendaService(
            clienteRepository,
            vendaRepository,
            pagamentoRepository,
            new PagamentoPixDAO(transacao),
            new PagamentoCartaoDAO(transacao),
            veiculoRepository,
            vendedorRepository,
            transacao);

    public ClienteService clienteService() { return clienteService; }
    public VeiculoService veiculoService() { return veiculoService; }
    public VendaService vendaService() { return vendaService; }
}
