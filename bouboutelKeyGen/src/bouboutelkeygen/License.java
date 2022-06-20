/*
 * License.java
 *
 * Created on October 23, 2005, 1:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package key;

import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author sdperret
 */
public class License implements Serializable{
    
    
    public static final long serialVersionUID = 772388273651L;
    
    /** Creates a new instance of License */
    
    public License(){
    }
    
    public License( String aProgramName, float aVersion, String aName, int aDay, int aMonth, int aYear, PrivateKey aPriv ) throws Exception { //throws java.security.NoSuchAlgorithmException{
        day = aDay;
        month = aMonth;
        year = aYear;
        name = aName;
        programName = aProgramName;
        version = aVersion;
        
        // compute signature
        byte[] license = getLicenseString().getBytes();
                                
                                
        // make signature string
        
        //Signature signatureMaker = Signature.getInstance("SHA1withRSA"); 
        //Signature signatureMaker = Signature.getInstance("MD5withRSA");
        Signature signatureMaker = Signature.getInstance("SHA1withDSA");
        signatureMaker.initSign(aPriv);
        signatureMaker.update( license ); 

        signature = signatureMaker.sign();     
    }
    
    public boolean checkSignature( File aFilename ) throws Exception {
        
        java.security.cert.Certificate myCertif;
        
        FileInputStream fis = new FileInputStream( aFilename );
        BufferedInputStream bis = new BufferedInputStream(fis);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        
        //while (bis.available() > 0) {
            myCertif = cf.generateCertificate(bis);
            System.out.println( myCertif.toString() );
        //}
        
        Signature signatureMaker = Signature.getInstance("SHA1withDSA"); 
        signatureMaker.initVerify( myCertif );
        signatureMaker.update( getLicenseString().getBytes() );
                                
        if ( signatureMaker.verify( signature ) ){
            // signature is good
            return true;
        }
        
        return false;
    }
    
    public String getLicenseString(){
        //return day + month + year + name;
        return day + month + year + name;
    }
    
    public void save( File aFile ) throws IOException {
        
        if( !aFile.exists() ){
           aFile.createNewFile(); 
        }
        
        ObjectOutputStream out =  new ObjectOutputStream( new FileOutputStream( aFile ) );
        
        try{            
            out.writeObject( this );
        }catch( java.io.NotSerializableException e ){
            System.out.println( "BookingSystem:save intercepted: " + e );
        }
        
        out.close();
    }
    
    
    public static License load( File aFile ) throws FileNotFoundException, IOException, ClassNotFoundException {
                   
        License aBS;
        ObjectInputStream in = new ObjectInputStream( new FileInputStream( aFile ) );   
            aBS = (License) in.readObject();
        in.close();
        
        return( aBS );
    }
      
    
    public String getProgramName(){
        return programName;
    }
    public float getVersion(){
        return version;
    }
    public Date getDate() throws java.text.ParseException{
        SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
        return sf.parse( day + "." + month + "." + year);
    }
    public String getName(){
        return name;
    }
      
    
    String name = "";
    int day = 0;
    int month = 0;
    int year = 0;
    byte[] signature;
    
    String programName = "";
    float version = 0;
    
    int securityCheck = 1;
}