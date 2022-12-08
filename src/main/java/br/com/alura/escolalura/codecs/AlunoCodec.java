package br.com.alura.escolalura.codecs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.models.Contato;
import br.com.alura.escolalura.models.Curso;
import br.com.alura.escolalura.models.Habilidade;
import br.com.alura.escolalura.models.Nota;

public class AlunoCodec implements CollectibleCodec<Aluno> {
	
	private Codec<Document> codec = MongoClient.getDefaultCodecRegistry().get(Document.class) ;
	
//	public AlunoCodec(Codec<Document> codec) {
//		this.codec = codec;
//	}

	//-----------------------------------------------------------------
	/** Define como transformar um Aluno em um Document */
	//-----------------------------------------------------------------
	@Override
	public void encode(BsonWriter writer, Aluno aluno, EncoderContext encoder) {
		// Recupera atributos do aluno
		ObjectId id = aluno.getId();
		String nome = aluno.getNome();
		Date dataNascimento = aluno.getDataNascimento();
		Curso curso = aluno.getCurso();
		List<Habilidade> listahabilidades = aluno.getHabilidades();
		List<Nota> listaNotas = aluno.getNotas();
		Contato contato = aluno.getContato();
		
		// Inicia a construcao de um Document
		Document document = new Document();		
		document.put("_id", id);
		document.put("nome", nome);
		document.put("data_nascimento", dataNascimento);
		document.put("curso", new Document("nome", curso.getNome()));
		
		// Mapeia habilidades
		if (listahabilidades != null) {
			List<Document> habilidadesDocument = new ArrayList<>();
			for (Habilidade habilidade : listahabilidades) {
				habilidadesDocument.add(
						new Document("nome", habilidade.getNome())
							.append("nivel", habilidade.getNivel()));				
			}
			document.put("habilidades", habilidadesDocument);
		}
		
		// Mapeia notas
		if (listaNotas != null) {
			List<Integer> notasParaSalvar = new ArrayList<>();
			for (Nota nota : listaNotas) {
				notasParaSalvar.add(nota.getValor());
			}
			document.put("notas", notasParaSalvar);			
		}
		
		// Recupera as coordenadas do contato
		List<Double> coordinates = new ArrayList<Double>();
		for(Double location : contato.getCoordinates()){
			coordinates.add(location);
		}
		
		// Mapeia contato
		document.put("contato", new Document()
				.append("endereco" , contato.getEndereco())
				.append("coordinates", coordinates)
				.append("type", contato.getType()));
		
		
		codec.encode(writer, document, encoder);
		
	}

	//-----------------------------------------------------------------
	/** Classe utilizada pelo conversor */
	//-----------------------------------------------------------------		
	@Override
	public Class<Aluno> getEncoderClass() {
		return Aluno.class;
	}

	//-----------------------------------------------------------------
	/** Define como transformar um Aluno em um Document */
	//-----------------------------------------------------------------	
	@Override
	public Aluno decode(BsonReader reader, DecoderContext decoder) {
		Document document = codec.decode(reader, decoder);
		
		Aluno aluno = new Aluno();		
		aluno.setId(document.getObjectId("_id"));
		aluno.setNome(document.getString("nome"));
		aluno.setDataNascimento(document.getDate("data_nascimento"));
		Document curso = (Document) document.get("curso");
		if (curso != null) {
			String nomeCurso = curso.getString("nome");
			aluno.setCurso(new Curso(nomeCurso));
		}
		
		// Mapeia notas
		List<Integer> listaNotas = (List<Integer>) document.get("notas");
		if (listaNotas != null) {
			List<Nota> notasDoAluno = new ArrayList<>();
			for (Integer nota : listaNotas) {
				notasDoAluno.add(new Nota(nota));
			}			
			aluno.setNotas(notasDoAluno);
		}
		
		// Mapeia habilidades
		List<Document> listaHabilidades = (List<Document>) document.get("habilidades");		
		if (listaHabilidades != null) {
			List<Habilidade> habilidadesDoAluno = new ArrayList<>();
			for (Document documentHabilidade : listaHabilidades) {
				String nome = documentHabilidade.getString("nome") ;
				String nivel = documentHabilidade.getString("nivel") ;
				habilidadesDoAluno.add(new Habilidade(nome, nivel));
			}
			aluno.setHabilidades(habilidadesDoAluno);
		}
		
		// Mapeia contato
		Document contato = (Document) document.get("contato");
		if (contato != null) {
			String endereco = contato.getString("contato");
			List<Double> coordinates = (List<Double>) contato.get("coordinates");
			aluno.setContato(new Contato(endereco, coordinates));
		}		
		
		return aluno;
	}

	@Override
	public boolean documentHasId(Aluno aluno) {
		return aluno.getId() != null;
	}

	@Override
	public Aluno generateIdIfAbsentFromDocument(Aluno aluno) {
		return documentHasId(aluno) ? aluno : aluno.criarId();
	}

	@Override
	public BsonValue getDocumentId(Aluno aluno) {
		if (!documentHasId(aluno)) {
			throw new IllegalStateException("Esse Document nao tem id");
		}
		
		return new BsonString(aluno.getId().toHexString());
	}

}
