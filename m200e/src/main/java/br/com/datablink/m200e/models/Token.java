package br.com.datablink.m200e.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Token {
	
	//{"token_serial":"galt00001234","token_app_version":"android-3.2","token_language":"pt_BR","token_imei":"testetestestestestestestesteste"}
	
	
	public static final int STATUS_AVAILABLE = 0;
		
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int token_id;
	@Column(name = "serial_number",unique=true)
	private String serial_number;
	private String data;
	private String auth_blob;
	@Column(name = "mobile_blob",length=1024)
	private String mobile_blob;
	private int token_status;
	private String token_app_version;
	private String token_language;
	private String token_imei;
	private String token_mobile_dna;
	@ManyToOne
	private Usuario token_owner;
	
	public void setData(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	
	public String getToken_mobile_dna() {
		return token_mobile_dna;
	}
	public void setToken_mobile_dna(String token_mobile_dna) {
		this.token_mobile_dna = token_mobile_dna;
	}
	public String getToken_app_version() {
		return token_app_version;
	}
	public void setToken_app_version(String token_app_version) {
		this.token_app_version = token_app_version;
	}
	public String getToken_language() {
		return token_language;
	}
	public void setToken_language(String token_language) {
		this.token_language = token_language;
	}
	public String getToken_imei() {
		return token_imei;
	}
	public void setToken_imei(String token_imei) {
		this.token_imei = token_imei;
	}
	public int getToken_status() {
		return token_status;
	}
	public void setToken_status(int token_status) {
		this.token_status = token_status;
	}
	public String getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	public String getAuth_blob() {
		return auth_blob;
	}
	public void setAuth_blob(String auth_blob) {
		this.auth_blob = auth_blob;
	}
	public String getMobile_blob() {
		return mobile_blob;
	}
	public void setMobile_blob(String mobile_blob) {
		this.mobile_blob = mobile_blob;
	}
	public int getToken_id() {
		return token_id;
	}
	public void setToken_id(int token_id) {
		this.token_id = token_id;
	}
	public Usuario getToken_owner() {
		return token_owner;
	}
	public void setToken_owner(Usuario token_owner) {
		this.token_owner = token_owner;
	}
	
	
	

	@Override
	public String toString() {
		return "Token [token_id=" + token_id + ", serial_number=" + serial_number + ", data=" + data + ", auth_blob="
				+ auth_blob + ", mobile_blob=" + mobile_blob + ", token_status=" + token_status + ", token_app_version="
				+ token_app_version + ", token_language=" + token_language + ", token_imei=" + token_imei
				+ ", token_mobile_dna=" + token_mobile_dna + ", token_owner=" + token_owner + "]";
	}

	public Token(){
		
	}
	
	public Token(String serial_number, String auth_blob, String mobile_blob, int token_status) {
		super();
		this.serial_number = serial_number;
		this.auth_blob = auth_blob;
		this.mobile_blob = mobile_blob;
		this.token_status = token_status;
	}
	
	

}
