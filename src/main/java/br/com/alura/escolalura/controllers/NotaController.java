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
import br.com.alura.escolalura.models.Nota;
import br.com.alura.escolalura.repositorys.AlunoRepository;

//----------------------------------------------------------------------------
/** Controlador que contem os métodos para cadastro e pesquisa de notas */
//----------------------------------------------------------------------------
@Controller
public class NotaController {
	
	@Autowired
	private AlunoRepository repository;
	
	//---------------------------------------------------
	/** Exibe a tela de cadastro de habilidade */
	//---------------------------------------------------	
	@GetMapping("/nota/cadastrar/{id}")
	public String cadastrar(@PathVariable String id, Model model){
		Aluno aluno = repository.pesquisarPorId(id);
		model.addAttribute("aluno", aluno);
		model.addAttribute("nota", new Nota());
		
		return "nota/cadastrar";
	}
	
	//---------------------------------------------------
	/** Salva uma nota */
	//---------------------------------------------------	
	@PostMapping("/nota/salvar/{id}")
	public String salvar(@PathVariable String id, @ModelAttribute Nota nota){
		Aluno aluno = repository.pesquisarPorId(id);
		aluno.adicionar(nota) ;
		repository.salvar(aluno);		
		return "redirect:/aluno/listar";
	}
	
	//---------------------------------------------------
	/** Exibe a tela de pesquisa por nota */
	//---------------------------------------------------	
	@GetMapping("/nota/iniciarpesquisa")
	public String iniciarPesquisa(){
		return "nota/pesquisar";
	}

	//---------------------------------------------------
	/** Faz a pesquisa por nota */
	//---------------------------------------------------	
	@GetMapping("/nota/pesquisar")
	public String pesquisarPor(@RequestParam("classificacao") String classificacao,
			@RequestParam("notacorte") String notaCorte, Model model){
		List<Aluno> alunos = repository.pesquisarPorNota(classificacao, Double.parseDouble(notaCorte));
		
		model.addAttribute("alunos", alunos);
		
		return "nota/pesquisar"; 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
