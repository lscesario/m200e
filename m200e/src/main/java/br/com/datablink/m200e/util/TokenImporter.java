package br.com.datablink.m200e.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.datablink.mgmtapi.DATABLINK_BLOB;
import com.datablink.mgmtapi.DatablinkMgmtAPI;
import com.datablink.mobmgmtapi.java.structs.MobileBlob;
import com.datablink.mobmgmtapi.java.wrapper.DatablinkMobileMgmtAPIWrapper;
import com.sun.jna.ptr.IntByReference;

import br.com.datablink.m200e.conf.ClientKey;
import br.com.datablink.m200e.daos.TokenDAO;
import br.com.datablink.m200e.models.Token;

public class TokenImporter {

	@Inject
	TokenDAO tokenDAO;
	
	Token token = new Token();
	
	@Transactional
	public String importTokens(String file_path, String transport_key){
		String additional_transport_key="";
		int ret=0;
		
		File seed_file= new File(file_path);
		Vector<String> seeds = readArquivoChaves(seed_file);
		
		for(int i=0; i < seeds.size(); i++){
			Token token = new Token();
			MobileBlob blob_mobile = new MobileBlob();
			
			
			String serial_number=seeds.get(i).substring(0, 12);
			String auth_blob=seeds.get(i).substring(12);
			DATABLINK_BLOB authBlob=new DATABLINK_BLOB();
			IntByReference tamBlob = new IntByReference();
			tamBlob.setValue(236);
			
			System.out.println("BRTDecriptaArquivoChaves.... numero_de_serie["+serial_number+"] blobArquivo["+auth_blob+"]");
			ret=DatablinkMgmtAPI.DatablinkDecryptKeyFile(serial_number, auth_blob, transport_key, additional_transport_key, tamBlob, authBlob);
			if(ret==0){
				System.out.println("Blob de authenticação gerado: "+authBlob.blob);
				token.setAuth_blob(authBlob.blob);
				token.setSerial_number(serial_number);
				token.setToken_status(Token.STATUS_AVAILABLE);
			}else{
				System.err.println("Erro: "+DatablinkMgmtAPI.DatablinkErrorMessage(ret, 0));
				return "error_on_importing_tokens";
			}
			
			ret=DatablinkMgmtAPI.DatablinkActivateToken(authBlob, true);
			if(ret==DatablinkMobileMgmtAPIWrapper.SUCCESS){
				System.out.println("Blob ativado com sucessso: "+(authBlob.blob));
				System.out.println("Blob size: "+authBlob.blob.length());
							
			}else{
				System.out.println("Erro ao ativar token: "+serial_number+":"+DatablinkMgmtAPI.DatablinkErrorMessage(ret, 0));
				return "error_on_activating_tokens";
			}
			
			StringBuilder blobMobile = new StringBuilder();
			ret=DatablinkMgmtAPI.DatablinkGenMobileMgmtAPIBlob(authBlob, ClientKey.CLIENT_KEY, DatablinkMgmtAPI.AppMOBILE200_GEN_2, blobMobile);
			if(ret==DatablinkMobileMgmtAPIWrapper.SUCCESS){
				System.out.println("Blob Mobile gerado com sucesso: "+blobMobile.toString());
				blob_mobile.setBlob(blobMobile.toString());	
			}else{
				System.err.println("Erro: "+DatablinkMgmtAPI.DatablinkErrorMessage(ret, 0));
				return "error_on_generating_mobile_blob";
			}
			
			ret=DatablinkMobileMgmtAPIWrapper.DatablinkActivate(blob_mobile, ClientKey.CLIENT_KEY, true);
			if(ret==DatablinkMobileMgmtAPIWrapper.SUCCESS){
				System.out.println("Blob Mobile ativado com sucesso: "+blob_mobile.getBlob());
				token.setMobile_blob(blob_mobile.getBlob());
			}else{
				System.err.println("Erro: "+DatablinkMgmtAPI.DatablinkErrorMessage(ret, 0));
				return "error_on_activating_mobile_blob";
			}
			String ret2=tokenDAO.createToken(token);
			if(!ret2.equals("error_on_persisting_token")){
				System.out.println("Token criado com sucesso: "+token.toString());
			}else{
				System.err.println("Erro ao persistir roken: "+ret2);
				return "error_on_persisting_token";
			}
		}
		return "everything_fine";
	}

	private static Vector<String> readArquivoChaves(File aFile){
		Vector<String> contents = new Vector<String>();
		try{
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try{
				String line = null;

				while((line = input.readLine()) != null){
					contents.add(line.replace('\n',' ').replace('\r',' ').trim());
				}
			}finally{
				input.close();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return contents;
	}
	
}
