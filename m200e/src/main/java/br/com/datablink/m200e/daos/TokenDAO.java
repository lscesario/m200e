package br.com.datablink.m200e.daos;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceException;

import br.com.datablink.m200e.models.Token;
import br.com.datablink.m200e.models.Usuario;

@Dependent
@Stateful
public class TokenDAO {
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager manager;
	
	
	public String createToken(Token token){
		System.out.println("Persisting Token: "+token.toString());
		try{
			manager.persist(token);		
			}catch(PersistenceException e){
			System.out.println("Token record could not be created");
			System.out.println(e);
			return "error_on_persisting_token";
		}
		return "token_"+token.getSerial_number().toString()+"_created";
	}

	public Token loadTokenById(int token_id){
		try{
		return manager.find(Token.class, token_id);
		}catch(NoResultException e){
			e.printStackTrace();
			System.out.println("TokenDAO - loadTokenById - Token Id não encontrado");
			return null;
		}
	}
	
	public void updateTokenBySerialNumber(String serial_number, Token token){
		Token token_to_be=findAuthBlobBySerialNumber(serial_number);
		System.out.println("TokenDAO - updateTokenBySerialNumber - Antes do update: "+token_to_be.toString());
		token_to_be=token;
		manager.merge(token_to_be);
		System.out.println("TokenDAO - updateTokenBySerialNumber - Valores após update: "+token_to_be.toString());
	}
	
	public Token findAuthBlobBySerialNumber(String serial_number) {
		try{
		 String query = "select a from Token a where a.serial_number=:serial_number";
		 		Token i = manager.createQuery(query, Token.class)
				 					.setParameter("serial_number", serial_number)
				 					.getSingleResult();
		 		return loadTokenById(i.getToken_id());
		}catch(NoResultException e){
			System.out.println("TokenDAO - findAuthBlobBySerialNumber - Serial não encontrado");
			e.printStackTrace();
			return null;
			}
		}
	
	void associateTokenToUser(Token token, Usuario user){
		
	}
}
