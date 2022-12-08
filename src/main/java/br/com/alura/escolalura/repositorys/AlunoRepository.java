package br.com.alura.escolalura.repositorys;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import br.com.alura.escolalura.codecs.AlunoCodec;
import br.com.alura.escolalura.models.Aluno;

//------------------------------------------------------------------------------------
/** Repositorio responsavel por executar pesquisas e cadastro no banco Mongo DB */
//------------------------------------------------------------------------------------
@Repository
public class AlunoRepository {
	
	private MongoClient cliente;
	
	//----------------------------------------------------
	/** Cria conexao com o Mongo DB */
	//----------------------------------------------------
	private MongoCollection<Aluno> criarConexao() {
		// Define mapeamento de Aluno para Document
		AlunoCodec alunoCodec = new AlunoCodec();

		// Adiciona o codec de aluno no registro
		CodecRegistry registro = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(alunoCodec));
		
		// Adiciona o registro nas opcoes para iniciar o MongoDB 
		MongoClientOptions opcoes = MongoClientOptions.builder().codecRegistry(registro).build();
		
		// Inicializa o Mongo
		this.cliente = new MongoClient("localhost:27017", opcoes);
		MongoDatabase database = cliente.getDatabase("test");
		
		MongoCollection<Aluno> colecaoAlunos = database.getCollection("alunos", Aluno.class);
		return colecaoAlunos ;
	}
	
	//----------------------------------------------------
	/** Fecha conexao com o Mongo DB */
	//----------------------------------------------------	
	private void fecharConexao() {
		this.cliente.close();
	}

	//----------------------------------------------------
	/** Salva um aluno no Mongo DB */
	//----------------------------------------------------		
	public void salvar(Aluno aluno) {		
		MongoCollection<Aluno> colecaoAlunos = criarConexao();		
		
		if (aluno.getId() == null) {
			colecaoAlunos.insertOne(aluno);
		}else{
			colecaoAlunos.updateOne(Filters.eq("_id", aluno.getId()), new Document("$set", aluno));
		}
		
		fecharConexao();
	}

	//----------------------------------------------------
	/** Recupera todos os alunos cadastrados */
	//----------------------------------------------------		
	public List<Aluno> pesquisarTodos() {
		MongoCollection<Aluno> colecaoAlunos = criarConexao();
				
		MongoCursor<Aluno> resultados = colecaoAlunos.find().iterator();
		
		List<Aluno> alunosEncontrados = carregarListaAlunos(resultados);
		fecharConexao();
		
		return alunosEncontrados;
	}
	
	//-------------------------------------------------------
	/** Carrega lista de alunos com a lista do Mongo */
	//-------------------------------------------------------
	private List<Aluno> carregarListaAlunos(MongoCursor<Aluno> resultados){
		List<Aluno> alunos = new ArrayList<>();
		while(resultados.hasNext()){
			alunos.add(resultados.next());
		}
		return alunos;
	}	
	
	//----------------------------------------------------
	/** Recupera o aluno que tem o id informado */
	//----------------------------------------------------	
	public Aluno pesquisarPorId(String id){
		MongoCollection<Aluno> colecaoAlunos = criarConexao();		
		Aluno aluno = colecaoAlunos.find(Filters.eq("_id", new ObjectId(id))).first();
		fecharConexao();
		return aluno;		
	}

	//----------------------------------------------------
	/** Recupera os alunos que tem o nome informado */
	//----------------------------------------------------	
	public List<Aluno> pesquisarPorNome(String nome) {
		MongoCollection<Aluno> colecaoAlunos = criarConexao();		
		MongoCursor<Aluno> resultados = colecaoAlunos.find(Filters.eq("nome", nome), Aluno.class).iterator();
		List<Aluno> alunos = carregarListaAlunos(resultados);
		fecharConexao();		
		return alunos;
	}

	//--------------------------------------------------------
	/** Faz a pesquisa de alunos por classificacao e nota */
	//--------------------------------------------------------	
	public List<Aluno> pesquisarPorNota(String classificacao, double nota) {
		MongoCollection<Aluno> colecaoAlunos = criarConexao();
		
		// Para reprovados procura notas < nota informada. Para aprovados procura notas >= nota informada.
		MongoCursor<Aluno> resultados = 
			classificacao.equals("reprovados") ? colecaoAlunos.find(Filters.lt("notas", nota)).iterator() :
			classificacao.equals("aprovados")  ? colecaoAlunos.find(Filters.gte("notas", nota)).iterator() :
			null ;
		
		List<Aluno> alunos = carregarListaAlunos(resultados);
		
		fecharConexao();
		
		return alunos;		
	}

	public List<Aluno> pesquisaPorGeolocalizacao(Aluno aluno) {
		MongoCollection<Aluno> colecaoAlunos =  criarConexao();
		
		colecaoAlunos.createIndex(Indexes.geo2dsphere("contato"));
		
		List<Double> coordinates = aluno.getContato().getCoordinates();
		Point pontoReferencia = new Point(new Position(coordinates.get(0), coordinates.get(1)));
		
		MongoCursor<Aluno> resultados = colecaoAlunos.find(Filters.nearSphere("contato", pontoReferencia, 2000.0, 0.0)).limit(2).skip(1).iterator();
		
		List<Aluno> alunos = carregarListaAlunos(resultados);
		
		fecharConexao();
		return alunos;
	}
	
	//----------------------------------------------
	/** Testa o repository 
	 *  No Mongo: 
	 *  - Executar mongo.exe
	 *  - Digitar: show databases
	 *  - Digitar: show collections 
	 *  - Digitar: db.alunos.find().pretty()
	 */
	//----------------------------------------------
	public static void main(String[] args) {
		for(Aluno aluno: new AlunoRepository().pesquisarTodos()) {
			System.out.println(aluno);
		}
	}
	
	
	
	

}
