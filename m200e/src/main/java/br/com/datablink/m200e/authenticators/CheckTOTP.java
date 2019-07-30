package br.com.datablink.m200e.authenticators;

import javax.inject.Inject;

import com.datablink.mgmtapi.DATABLINK_BLOB;
import com.datablink.mgmtapi.DatablinkMgmtAPI;
import com.datablink.mgmtapi.device200.DEVICE200_INFO;

import br.com.datablink.m200e.daos.TokenDAO;

public class CheckTOTP {
	
	@Inject
	TokenDAO tokenDAO;
	
	public String authenticateTOTP(String serial_number, String OTP ){
		DATABLINK_BLOB blobAutenticacao = new DATABLINK_BLOB();
		blobAutenticacao.blob=tokenDAO.findAuthBlobBySerialNumber(serial_number).getAuth_blob();		
		//POR QUE ISSO DE NOVO???
		int DatablinkMgmtAPIActResult = DatablinkMgmtAPI.DatablinkActivateToken(blobAutenticacao, true);
		if(DatablinkMgmtAPIActResult!=0){
			System.out.println("Erro na ativação do Token: "+DatablinkMgmtAPIActResult+" Motivo: "+DatablinkMgmtAPI.DatablinkErrorMessage(DatablinkMgmtAPIActResult, 0));
		}
		int DatablinkCheckOTPResult = DatablinkMgmtAPI.DatablinkCheckOTP(blobAutenticacao,OTP);	
		if(DatablinkCheckOTPResult!=0){
			System.out.println("AutenticarSenhaOTP Error : "+DatablinkCheckOTPResult+" Motivo: "+DatablinkMgmtAPI.DatablinkErrorMessage(DatablinkCheckOTPResult,0));
			return "authetication_error";
		}
		DEVICE200_INFO dv200 = new DEVICE200_INFO();
		System.out.println("Device 200: "+DatablinkMgmtAPI.DatablinkReadDevice200Info(blobAutenticacao, dv200));
		return "authentication_ok";
	}
}
