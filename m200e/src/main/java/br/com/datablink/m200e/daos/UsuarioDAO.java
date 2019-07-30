package br.com.datablink.m200e.daos;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceException;

import br.com.datablink.m200e.models.Token;
import br.com.datablink.m200e.models.Usuario;

public class UsuarioDAO {
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager manager;
	
	public String createUser(Usuario user){
		System.out.println("Persisting Token: "+user.toString());
		try{
			manager.persist(user);		
			}catch(PersistenceException e){
			System.out.println("User record could not be created");
			System.out.println(e);
			return "error_on_persisting_user";
		}
		return "token_"+user.getUser_id()+"_created";
	}
	
	public Usuario loadUserById(int user_id){
		try{
		return manager.find(Usuario.class, user_id);
		}catch(NoResultException e){
			e.printStackTrace();
			System.out.println("TokenDAO - loadUserById - User Id não encontrado");
			return null;
		}
	}
}
