package dk.cphbusiness.flightdemo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.cphbusiness.utils.Utils;
import lombok.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class FlightReader {

    public static void main(String[] args) {
        FlightReader flightReader = new FlightReader();
        try {
            List<DTOs.FlightDTO> flightList = flightReader.getFlightsFromFile("flights.json");
            List<DTOs.FlightInfo> flightInfoList = flightReader.getFlightInfoDetails(flightList);

            System.out.println(flightReader.getAvgFlightTime());

            /*flightInfoList.forEach(f->{
                System.out.println("\n"+f);
            });*/
          
            flightReader.totalFlightTime(flightInfoList);
          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public List<FlightDTO> jsonFromFile(String fileName) throws IOException {
//        List<FlightDTO> flights = getObjectMapper().readValue(Paths.get(fileName).toFile(), List.class);
//        return flights;
//    }

    public void totalFlightTime(List<DTOs.FlightInfo> flightInfoList) {
        Duration totalFlightTime = flightInfoList.stream()
                .map(flight -> flight.getDuration())
                .reduce((flightSum, flightTime) -> flightSum.plus(flightTime))
                .orElse(Duration.ZERO);

        System.out.println("Total flight time of all flights: \n" +
                "In days: " + totalFlightTime.toDays() +
                " days \nIn hours: " + totalFlightTime.toHours() +
                " hours \nIn minutes: " + totalFlightTime.toMinutes() +
                " minutes \nIn seconds: " + totalFlightTime.toSeconds() +
                " seconds \nIn nanoseconds: " + totalFlightTime.toNanos() + " nanoseconds");

    }


    public List<DTOs.FlightInfo> getFlightInfoDetails(List<DTOs.FlightDTO> flightList) {
        List<DTOs.FlightInfo> flightInfoList = flightList.stream().map(flight -> {
            Duration duration = Duration.between(flight.getDeparture().getScheduled(), flight.getArrival().getScheduled());
            DTOs.FlightInfo flightInfo = DTOs.FlightInfo.builder()
                    .name(flight.getFlight().getNumber())
                    .iata(flight.getFlight().getIata())
                    .airline(flight.getAirline().getName())
                    .duration(duration)
                    .departure(flight.getDeparture().getScheduled().toLocalDateTime())
                    .arrival(flight.getArrival().getScheduled().toLocalDateTime())
                    .origin(flight.getDeparture().getAirport())
                    .destination(flight.getArrival().getAirport())
                    .build();

            return flightInfo;
        }).toList();
        return flightInfoList;
    }

    public List<DTOs.FlightDTO> getFlightsFromFile(String filename) throws IOException {
        DTOs.FlightDTO[] flights = new Utils().getObjectMapper().readValue(Paths.get(filename).toFile(), DTOs.FlightDTO[].class);

        List<DTOs.FlightDTO> flightList = Arrays.stream(flights).toList();
        return flightList;
    }


    // avg. flight time for all flight combined

    public Long getAvgFlightTime() throws IOException {

        List<DTOs.FlightDTO> flightDTOList = getFlightsFromFile("flights.json");
        List<DTOs.FlightInfo> flightInfoList = getFlightInfoDetails(flightDTOList);

        Long avgFlightTime = flightInfoList.stream()
                .map(flight -> flight.getDuration().getSeconds())
                .reduce((durationNow, durationNext) -> durationNow + durationNext).get();



//        //-------------------------- testing method to se if we get the first 10 flights and the seconds is right---------------------------------------------
//        // step 1 = limit. Step 2 = vis kun duration og lav om til seconds. Step 3 = Manuelt efterregn.
//        List<DTOs.FlightDTO> flightList2 = getFlightsFromFile("flights.json");
//        List<DTOs.FlightInfo> flightInfoList2 = getFlightInfoDetails(flightList2);
//
//        long avgFlightTime = flightInfoList2.stream()
//                .limit(10)
//                .map(flight -> flight.getDuration().getSeconds())
//                .reduce((sum1 , sum2) -> sum1 + sum2).get();
        return avgFlightTime;
    }

}
