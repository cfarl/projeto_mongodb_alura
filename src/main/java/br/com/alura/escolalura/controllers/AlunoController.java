package br.com.alura.escolalura.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.repositorys.AlunoRepository;
import br.com.alura.escolalura.service.GeolocalizacaoService;

//----------------------------------------------------------------------------
/** Controlador que contem os métodos para cadastro e pesquisa de alunos */
//----------------------------------------------------------------------------
@Controller
public class AlunoController {
	
	@Autowired
	private AlunoRepository repository;
	
	@Autowired
	private GeolocalizacaoService geolocalizacaoService;
	
	//---------------------------------------------------
	/** Exibe a tela de cadastro de aluno */
	//---------------------------------------------------
	@GetMapping("/aluno/cadastrar")
	public String cadastrar(Model model){
		model.addAttribute("aluno", new Aluno()); 
		return "aluno/cadastrar";
	}
	
	//---------------------------------------------------
	/** Salva o aluno */
	//---------------------------------------------------	
	@PostMapping("/aluno/salvar")
	public String salvar(@ModelAttribute Aluno aluno){
		System.out.println("Aluno para salvar: " + aluno);
		try {
			List<Double> latElong = geolocalizacaoService.obterLatELongPor(aluno.getContato());
			aluno.getContato().setCoordinates(latElong);
			
		} catch (Exception e) {
			System.out.println("Endereco nao localizado");
			e.printStackTrace();
		} 
		repository.salvar(aluno);
		
		return "redirect:/";
	}
	
	//------------------------------------------------------
	/** Encaminha para pagina que mostra todos os alunos */
	//------------------------------------------------------	
	@GetMapping("/aluno/listar")
	public String listar(Model model){
		List<Aluno> alunos = repository.pesquisarTodos();
		model.addAttribute("alunos", alunos);
		return "aluno/listar";
	}
	
	//-------------------------------------------------------------
	/** Encaminha para pagina que mostra os dados de um aluno */
	//-------------------------------------------------------------	
	@GetMapping("/aluno/visualizar/{id}")
	public String visualizar(@PathVariable String id, Model model){		
		Aluno aluno = repository.pesquisarPorId(id);		
		model.addAttribute("aluno", aluno);		
		return "aluno/visualizar";
	}
	
	//-------------------------------------------------------------
	/** Encaminha para pagina que pesquisa alunos por nome */
	//-------------------------------------------------------------		
	@GetMapping("/aluno/pesquisarnome")
	public String pesquisarNome(){
		return "aluno/pesquisarnome";
	}

	//-------------------------------------------------------------
	/** Faz a pesquisa de alunos por nome */
	//-------------------------------------------------------------		
	@GetMapping("/aluno/pesquisar")
	public String pesquisar(@RequestParam("nome") String nome, Model model){
		List<Aluno> alunos = repository.pesquisarPorNome(nome);
		model.addAttribute("alunos", alunos);
		return "aluno/pesquisarnome"; 
	}		

}
