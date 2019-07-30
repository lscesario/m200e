package br.com.datablink.m200e.activator;

import java.util.Iterator;
import java.util.Vector;

import javax.inject.Inject;

import org.bouncycastle.util.encoders.Hex;

import com.datablink.mgmtapi.DATABLINK_BLOB;
import com.datablink.mgmtapi.DatablinkMgmtAPI;
import com.datablink.mobmgmtapi.java.structs.CommunicationResult;
import com.datablink.mobmgmtapi.java.structs.MobileBlob;
import com.datablink.mobmgmtapi.java.wrapper.DatablinkMobileMgmtAPIWrapper;

import br.com.datablink.m200e.configuration.Configuration;
import br.com.datablink.m200e.daos.TokenDAO;
import br.com.datablink.m200e.daos.UsuarioDAO;
import br.com.datablink.m200e.models.Token;

public class ActivationControl {
	
	@Inject
	TokenDAO tokenDAO;
	@Inject
	UsuarioDAO userDAO;

	public String activateStep1(Token token) {
		final String TAG="ActivationControl - activateStep1 - ";
		
		System.out.println(TAG+" Iniciando serviço interno de ativação step1");
		
		String pacote = token.getData();
		
		System.out.println((new String(Hex.decode(Configuration.CHAVE_CLIENTE))).toString());
		
		Vector<String> oData = new Vector<>();
		int extractDataResult =  DatablinkMobileMgmtAPIWrapper.DatablinkExtractDataCommunication(Configuration.CHAVE_CLIENTE, pacote, oData, "1");
		if(extractDataResult !=0){
			return "";
		}else{
			for (String string : oData) {
				System.out.println(string);
			}
		}
		
		
		int resultFunction = 0;
		
		//IMPLEMENTAR PROTOCOLO DE PROTEÇÃO DTB
		
		//COMPOSIÇÃO DO TOKEN, BAIXO AO OBJETO RECEBIDO PELO JSON DO APP MOBILE
		System.out.println(TAG+ "- Parâmetros do token recebidos" + token.toString());
		
		String token_language = oData.get(5);
		String token_app_version = oData.get(4);
		String token_imei = oData.get(3);
		String token_serial_number = oData.get(2);
		String token_mobile_dna = oData.get(1);

		if(token_serial_number.equals(null)){
			return "invalid_or_null_serial_number";
		}
		
		token=tokenDAO.findAuthBlobBySerialNumber(token_serial_number);
		if(token==null){
			return "token_serial_number_inexistent";
		}
		
		if(token.getToken_status()!=Configuration.TOKEN_STATUS_AVAILABLE){
			return "token_already_assigned";
		}
		
		System.out.println(TAG+" - Parâmetros do token recuperados" + token.toString());
		
		token.setToken_app_version(token_app_version);
		token.setToken_imei(token_imei);
		token.setToken_language(token_language);
		token.setToken_mobile_dna(token_mobile_dna);
		//OBJETO TOKEN FINALIZADO E PRONTO PARA SER TRABALHADO
		System.out.println(TAG+" - Parâmetros do token com propriedades mobile" + token.toString());
		
	
		//ATIVAR BLOB DE AUTENTICAÇÃO PARA O TOKEN
		DATABLINK_BLOB blobAutenticacao = new DATABLINK_BLOB();
		//OBTENDO O BLOB DE AUTENTICACAO 
		blobAutenticacao.blob = token.getAuth_blob();
		resultFunction=DatablinkMgmtAPI.DatablinkActivateToken(blobAutenticacao, true);
		if(resultFunction!=0){
			System.out.println(TAG+" - Erro ao ativar o token: "+ resultFunction);
			return "error_activating_token";
		}
		System.out.println(TAG+" - AuthBlob Ativado");
		
		//DEFINIR O DNA DO MOBILE NO BLOB DE AUTENTICAÇÃO
		resultFunction=DatablinkMgmtAPI.DatablinkDefineMobileDNA(blobAutenticacao, token.getToken_mobile_dna());
		if(resultFunction!=0){
			System.out.println(TAG+" - Erro ao definir o Mobile DNA: "+ resultFunction);
			return "error_defining_mobile_dna";
		}
		System.out.println(TAG+" - Mobile DNA Definido");
		
		//ZERAR DESVIO DE AUTENTICAÇÃO
		resultFunction=DatablinkMgmtAPI.DatablinkResetTimeDrift(blobAutenticacao);
		if(resultFunction!=0){
			System.out.println(TAG+" - Erro ao zerar desvio de autenticação: "+ resultFunction);
			return "error_drift_reset";
		}
		System.out.println(TAG+" - Desvio de Autenticação Zerado");
		
		//ABRIR A JANELA DE AUTENTICAÇÃO
		resultFunction=DatablinkMgmtAPI.DatablinkOpenTokenWindow(blobAutenticacao, 2);
		if(resultFunction!=0){
			System.out.println(TAG+" - Erro ao abrir a janela de autenticação: "+ resultFunction);
			return "error_authentication_window_opening";
		}
		System.out.println(TAG+" - Janela de autenticação aberta");
		
		
		//OBTER BLOB MOBILE DO TOKEN
		MobileBlob mobileBlob = new MobileBlob(token.getMobile_blob());
		CommunicationResult response = new CommunicationResult();
		
		//EXECUTAR O ACTIVATE MOBILE STEP 1
		
		Vector customData = new Vector<>();
		resultFunction=DatablinkMobileMgmtAPIWrapper.DatablinkActivateMobileStep1(mobileBlob, 
																				  Configuration.CHAVE_CLIENTE, 
																				  oData.get(0), 
																				  customData, 
																				  response);
		if(resultFunction!=0){
			System.out.println(TAG+" - Erro ao ativar step1: "+ resultFunction);
			return "error_activate_step1";
		}
		System.out.println(TAG+" - Step 1 executado com sucesso");
		
		token.setAuth_blob(blobAutenticacao.blob);
		token.setMobile_blob(mobileBlob.getBlob());
		
		//ATUALIZAR BANCO DE DADOS
		tokenDAO.updateTokenBySerialNumber(token.getSerial_number(), token);
		//ENVIAR A RESPOSTA AO APP MOBILE
		return response.getResult();
	}

	public String activateStep2(Token token) {
		
		final String TAG="ActivationControl - activateStep2 - ";
		
		System.out.println(TAG+" Iniciando serviço interno de ativação step2");
		
		String pacote = token.getData();
		Vector<String> oData = new Vector<>();
		int extractDataResult =  DatablinkMobileMgmtAPIWrapper.DatablinkExtractDataCommunication(Configuration.CHAVE_CLIENTE, pacote, oData, "1");
		if(extractDataResult !=0){
			return "";
		}else{
			for (String string : oData) {
				System.out.println(string);
			}
		}
		
		String token_serial_number=oData.get(0);
		String token_dna_mobile=oData.get(1);
		String token_otp=oData.get(2);
		
		

		token=tokenDAO.findAuthBlobBySerialNumber(token_serial_number);
		if(token==null){
			return "token_serial_number_inexistent";
		}
		
		token.setToken_mobile_dna(token_dna_mobile);
		
		if(token.getToken_status()!=Configuration.TOKEN_STATUS_AVAILABLE){
			return "token_already_assigned";
		}
		
		DATABLINK_BLOB blobAutenticacao = new DATABLINK_BLOB();
		blobAutenticacao.blob = token.getAuth_blob();
		
		System.out.println(TAG+" - Parâmetros do token recuperados" + token.toString());
		
		int DatablinkCheckOTPResult = DatablinkMgmtAPI.DatablinkCheckOTP(blobAutenticacao,token_otp);			
		if(DatablinkCheckOTPResult!=0){
			System.out.println(TAG + "Erro na autenitcação da OTP on Step2");
			return "token_incorrect_otp";
		}
		
		MobileBlob mobileBlob = new MobileBlob(token.getMobile_blob());
		CommunicationResult response = new CommunicationResult();
		Vector customData = new Vector<>();
		
		int DatablinkActivateStep2 = DatablinkMobileMgmtAPIWrapper.DatablinkActivateMobileStep2(mobileBlob, 
																								Configuration.CHAVE_CLIENTE, 
																								pacote, 
																								token_otp, 
																								customData, 
																								response);
		if(DatablinkActivateStep2!=DatablinkMobileMgmtAPIWrapper.SUCCESS){
			System.out.println(TAG + "Erro na Ativação: "+DatablinkActivateStep2);
			return "activation_error";
		}
		
		//ASSOCIAR O MOBILE TOKEN AO USUARIO
		token.setToken_owner(userDAO.loadUserById(Configuration.USER_ID_MOCK));
		//ATUALIZAR TOKEN NA BASE DE DADOS
		tokenDAO.updateTokenBySerialNumber(token.getSerial_number(), token);
		return "activation_complete";
	}
}
