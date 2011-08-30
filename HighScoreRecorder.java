import java.net.*;
import java.io.*;
import java.util.*;
/**
 * Client side High Score Recorder
 */
class HighScoreRecorder {
    String host;
    String cgi;
    Socket socket=null;
    DataInputStream is;
    DataOutputStream os;

    public HighScoreRecorder(String ahost, String aCGI) { host = ahost; cgi=aCGI; }

    public Vector saveScore(String n, String s) { Vector v = null; try { System.out.println("saving Score"); openConnection(); postCGI(cgi, makeScore(n, s)); v = readAllLines(); closeConnection(); } catch (UnknownHostException e) { System.out.println("Unknown Host "+host); } catch (IOException e) { System.out.println("IOException"); } catch (NullPointerException e) { System.out.println("NullPointer Exception"); } return v; }

    public Vector getScores() { Vector v = null; try { openConnection(); getCGI(cgi); v = readAllLines(); closeConnection(); } catch (UnknownHostException e) { System.out.println("Unknown Host "+host); } catch (IOException e) { System.out.println("IOException"); } catch (NullPointerException e) { System.out.println("NullPointer Exception"); } return v; }

    void openConnection() throws UnknownHostException, IOException { System.out.println("opening CONNECTION"); socket=new Socket(host,80); System.out.println("Socket "+socket); os=new DataOutputStream(socket.getOutputStream()); is=new DataInputStream(socket.getInputStream()); }

    void closeConnection() throws IOException { is.close(); os.close(); socket.close(); }

    String makeScore(String name, String score) { return score+" "+name+"\n"; }

    void postCGI(String CGI, String str) throws IOException { os.writeBytes("POST "+CGI+" HTTP/1.0\n"); os.writeBytes("Content-Type: plain/text\n"); os.writeBytes("Content-Length: "+str.length()+"\n\n"); os.writeBytes(str); }

    void getCGI(String CGI) throws IOException { os.writeBytes("GET "+CGI+"\n"); os.writeBytes("\n"); }

    Vector readAllLines() throws IOException { String t; Vector v = new Vector(); while((t = is.readLine()) != null) v.addElement(t); return v; } }

