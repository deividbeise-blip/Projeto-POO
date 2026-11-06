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

    private final ClienteRepository clienteRepository = new ClienteDAO();
    private final ConcessionariaRepository concessionariaRepository = new ConcessionariaDAO();
    private final VeiculoRepository veiculoRepository = new VeiculoDAO();
    private final VendedorRepository vendedorRepository = new VendedorDAO();
    private final VendaRepository vendaRepository = new VendaDAO();
    private final PagamentoRepository pagamentoRepository = new PagamentoDAO();

    private final ClienteService clienteService = new ClienteService(clienteRepository);
    private final VeiculoService veiculoService = new VeiculoService(veiculoRepository);
    private final VendaService vendaService = new VendaService(
            clienteRepository,
            vendaRepository,
            pagamentoRepository,
            new PagamentoPixDAO(),
            new PagamentoCartaoDAO(),
            veiculoRepository);

    public ClienteService clienteService() { return clienteService; }
    public VeiculoService veiculoService() { return veiculoService; }
    public VendaService vendaService() { return vendaService; }

    public ClienteRepository clienteRepository() { return clienteRepository; }
    public ConcessionariaRepository concessionariaRepository() { return concessionariaRepository; }
    public VeiculoRepository veiculoRepository() { return veiculoRepository; }
    public VendedorRepository vendedorRepository() { return vendedorRepository; }
}
