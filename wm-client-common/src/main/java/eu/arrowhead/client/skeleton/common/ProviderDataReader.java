package eu.arrowhead.client.skeleton.common;


import java.io.FileReader;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;



import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;


@Component
public class ProviderDataReader {
    

    private List<WMDataObject> wmDataList;

    private CSVReader csvReader;


    //Not pretty with hardcoded file input
    //TODO: fix hardcoded file path
    public  ProviderDataReader () throws IOException, URISyntaxException {
        csvReader = new CSVReader(new FileReader("/home/robin/Downloads/DATASET02.csv"));

               
    }
    
    // Read one data point from the csv file
    public WMDataObject readData() throws IOException, URISyntaxException {

        WMDataObject wmData = new WMDataObject();
        try {
            String [] nextLine;
            if((nextLine = csvReader.readNext()) != null) {
                wmData.setWMTimeStamp(Double.valueOf(nextLine[0]));
                wmData.setWMSpeed(Double.valueOf(nextLine[1]));
                List<Double> wmAcc = new ArrayList<Double>();
                for(int i = 2; i < 16385; i++) {
                    
                    wmAcc.add(Double.valueOf(nextLine[i]));
                    
                }
                wmData.setWMAccelerometer(wmAcc);
                return wmData;
            }
            
            return wmData;
            
        } catch (Exception ex) {
            throw ex;
        } finally {
            
        }
    }
    public List<WMDataObject> getWMDataList() {return this.wmDataList;}

}
