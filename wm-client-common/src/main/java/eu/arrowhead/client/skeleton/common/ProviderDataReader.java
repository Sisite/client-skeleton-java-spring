package eu.arrowhead.client.skeleton.common;


import java.io.FileReader;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;


// import javax.annotation.PostConstruct;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
// import eu.arrowhead.client.skeleton.provider.common.WMDataObject;

// import eu.arrowhead.client.library.util.ClientCommonConstants;

@Component
public class ProviderDataReader {
    //private static final String TURBINE_DATA_CSV = "DATASET02.csv";

    private List<WMDataObject> wmDataList;

    private CSVReader csvReader;


    // @PostConstruct
    // public void init() throws IOException, URISyntaxException {
    //     this.wmDataList = new ArrayList<WMDataObject>();
    //     readData();
    //     //System.out.println(Arrays.toString(wmAccelerometer.toArray()));
    // }
    public  ProviderDataReader () throws IOException, URISyntaxException {
        csvReader = new CSVReader(new FileReader("/home/robin/Downloads/DATASET02.csv"));

               
    }

    public WMDataObject readData() throws IOException, URISyntaxException {
        //final Resource resource = new ClassPathResource(TURBINE_DATA_CSV);
        //final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
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
                // System.out.println(wmData.getWMAccelerometer().toString());
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
