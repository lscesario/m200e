package br.com.datablink.m200e.activator;

import javax.inject.Inject;
import javax.transaction.Transactional;

import br.com.datablink.m200e.daos.UsuarioDAO;
import br.com.datablink.m200e.models.Usuario;

public class UsuarioControl {

	@Inject
	UsuarioDAO usuarioDAO;
	
	@Transactional
	public void createUser(Usuario user) {
		try{
		usuarioDAO.createUser(user);
		}catch(NullPointerException e){
			e.printStackTrace();
			System.out.println("Error creating user");
		}
		System.out.println("User created");
	}
}
