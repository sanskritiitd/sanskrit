package cse.iitd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;


public class SandhiMaker
{
    private String      sandhiKrt;

    private String      sandhiNotes;

    public SandhiMaker(String p1, String p2, boolean padanta, boolean pragrhya)
    {
        sandhiNotes = "No Notes";
        sandhiKrt = combineIntoSandhi(p1, p2, padanta, pragrhya);
    }

    public String combineIntoSandhi(String anta, String adi, boolean padanta, boolean pragrhya)
    {

        Log.logInfo(" anta == " + anta + " adi == " + adi);
        String returnString = anta + adi;

        if (anta == null || adi == null || adi.length() == 0 || anta.length() == 0) return returnString;

        try
        {

            if (VowelUtil.isAjanta(anta) && VowelUtil.isAjadi(adi))
            {
                Log.logInfo(" Sending for Vowel Sandhi");
                VowelSandhi vowelSandhi = new VowelSandhi(anta, adi, pragrhya);
                returnString = vowelSandhi.getCombinedSandhiForm();
                sandhiNotes = vowelSandhi.getNotes();
            }

            else if (VisargaUtil.isVisarganta(anta))
            {
                Log.logInfo(" Sending for Visarga Sandhi");
                VisargaDisplay visargaSandhi = new VisargaDisplay(anta, adi, padanta, pragrhya);
                returnString = visargaSandhi.getCombinedSandhiForm();
                sandhiNotes = visargaSandhi.getNotes();
                Log.logInfo(" Quitting Visarga Sandhi: " + returnString);
            }

            else if (ConsonantUtil.is_halanta(anta) || ConsonantUtil.is_haladi(adi))
            {
                Log.logInfo(" Sending for Consonant Sandhi");
                ConsonantSandhi consonantSandhi = new ConsonantSandhi(anta, adi, padanta);
                returnString = consonantSandhi.getCombinedSandhiForm();
                sandhiNotes = consonantSandhi.getNotes();
                Log.logInfo(" Quitting Consonant Sandhi: " + returnString);
            }

            else if ((ConsonantUtil.is_halanta(anta) || VowelUtil.isAjanta(anta)) && (VowelUtil.isAjadi(adi) || ConsonantUtil.is_haladi(adi)))

            {
                Log.logInfo(" Sending for Vowel-Consonant Sandhi");
                VisargaDisplay vs = new VisargaDisplay(anta, adi, padanta, pragrhya);
                returnString = vs.getCombinedSandhiForm();
                sandhiNotes = vs.getNotes();
            }

            Log.logInfo("i m leaving sandhimaker.make_sandhi::::" + returnString);

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
        return returnString;

    }

    public String getSandhiCombinedForm()
    {
        return sandhiKrt;
    }

    // *******************END OF FUNCTION**********************//

    // *******************BEGINNING OF FUNCTION********************//
    public String getSandhiNotes()
    {
        return sandhiNotes;
    }


    private static void convertFromFile(String filePath) throws IOException {
		File fileDir = new File(filePath);
		String outFile = filePath+".out";
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));

		String line;
		
		while ((line = in.readLine()) != null) {
			String merge = line.split("\t")[3];
			
			String[] split = line.split("\t")[4].split("xxx");
			System.out.println(split.toString());
			
			//String tf1InSLP = EncodingUtil.convertToSLP("वृद्धिः", "DVN");
	        //String tf2InSLP = EncodingUtil.convertToSLP("आदैच्", "DVN");
			
			String tf1InSLP = EncodingUtil.convertToSLP(split[0], "DVN");
	        String tf2InSLP = EncodingUtil.convertToSLP(split[1], "DVN");

	        // convert anta + adi into their Sandhied form(s)
	        SandhiMaker sandhiMaker = new SandhiMaker(tf1InSLP, tf2InSLP, false, false);

	        String sandhiMergedForm = sandhiMaker.getSandhiCombinedForm(); // Merge the Two Words
	        //Log.logInfo("sandhiMerged Form " + sandhiMergedForm);
	        String outMerge = EncodingUtil.convertSLPToDevanagari(sandhiMergedForm);
	        
	        
			
			if(merge.trim().equalsIgnoreCase(outMerge.trim()))
				out.write(line +"\t"+outMerge+"\t1\n");
			else if(outMerge.contains(",")){
				String[] splits = outMerge.split(",");
				boolean eq = false;
				for(String sp : splits){
					if(merge.trim().equalsIgnoreCase(sp.trim())){
						out.write(line +"\t"+outMerge+"\t1\n");
						eq = true;
						break;
					} 
				}
				if (!eq)
					out.write(line +"\t"+outMerge+"\t0\n");
			}else
				out.write(line +"\t"+outMerge+"\t0\n");
		}
		in.close();
		out.close();
	}
    public static void main(String[] args) throws IOException {
    	String inFile = "C:\\Users\\IBM_ADMIN\\Desktop\\IITD\\sanskritResources\\astadhya\\astadhyaCorpus.txt";
    	convertFromFile(inFile);
	}

} // end of class
