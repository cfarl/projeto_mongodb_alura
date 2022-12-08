package br.com.alura.escolalura.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.models.Habilidade;
import br.com.alura.escolalura.repositorys.AlunoRepository;

//------------------------------------------------------------------------------
/** Controlador que contem os métodos para cadastro e pesquisa de habilidades */
//------------------------------------------------------------------------------
@Controller
public class HabilidadeController {
	
	@Autowired
	private AlunoRepository repository;

	//---------------------------------------------------
	/** Exibe a tela de cadastro de habilidade */
	//---------------------------------------------------
	@GetMapping("/habilidade/cadastrar/{id}")
	public String cadastrar(@PathVariable String id, Model model) {		
		Aluno aluno = repository.pesquisarPorId(id);
		model.addAttribute("aluno", aluno);
		model.addAttribute("habilidade", new Habilidade());
		
		return "habilidade/cadastrar";
	}
	
	//---------------------------------------------------
	/** Salva uma habilidade */
	//---------------------------------------------------
	@PostMapping("/habilidade/salvar/{id}")
	public String salvar(@PathVariable String id, @ModelAttribute Habilidade habilidade) {		
		Aluno aluno = repository.pesquisarPorId(id);
		aluno.adicionar(habilidade) ;
		repository.salvar(aluno);
		
		return "redirect:/aluno/listar";
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
}
