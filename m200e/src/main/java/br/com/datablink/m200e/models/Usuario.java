package br.com.datablink.m200e.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int user_id;
	private String user_name;
	@OneToMany
	private List<Token> user_tokens;
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public List<Token> getUser_tokens() {
		return user_tokens;
	}
	public void setUser_tokens(List<Token> user_tokens) {
		this.user_tokens = user_tokens;
	}
	
	@Override
	public String toString() {
		return "Usuario [user_id=" + user_id + ", user_name=" + user_name + ", user_tokens=" + user_tokens + "]";
	}
	
	
}
