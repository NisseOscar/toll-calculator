
import java.util.*;
import java.util.concurrent.*;

public class TollCalculator {
  // Set of all vehicles being toll free.
  // Preferably one would store tollFreevehicles in a file or database, principle is the same.
  String[] tollFreeVehicles = {"Motorbike","Tractor","Emergency","Diplomat","Foreign","Military"};
  private Set<String> TollFreeVehicles = new HashSet<String>(Arrays.asList(tollFreeVehicles));
  private Calendar calndr = GregorianCalendar.getInstance();

  /**
   * Calculate the total toll fee for one day.
   * @param vehicle - the vehicle
   * @param dates   - date and time of all passes on one day
   * @return - the total toll fee for that day
   */
  public int getTollFee(Vehicle vehicle, Date... dates) {
    if(vehicle == null || dates == null || dates.length==0) return 0;
    //  Chek date[0] since input is only from one day.
    if(isTollFreeVehicle(vehicle) || isTollFreeDate(dates[0])) return 0;
    Date intervalStart = dates[0];
    int tempFee = getTollFee(intervalStart);
    int totalFee = tempFee;
    int nextFee;
    long diffInMillies,diffMinutes;
    TimeUnit timeUnit = TimeUnit.MINUTES;
    for (Date date : dates) {
      nextFee = getTollFee(date);
      diffInMillies = date.getTime() - intervalStart.getTime();
      diffMinutes = timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
      if (diffMinutes < 60) {
        if(nextFee > tempFee){
          totalFee -= tempFee;
          totalFee += nextFee;
          tempFee = nextFee;
        }
      } else {
        totalFee += nextFee;
        intervalStart = date;
        tempFee = nextFee;
      }
      if (totalFee >= 60) return 60;
    }
    return totalFee;
  }

  private boolean isTollFreeVehicle(Vehicle vehicle) {
    return TollFreeVehicles.contains(vehicle.getType());
  }

  public int getTollFee(Date date){
    calndr.setTime(date);
    int hour = calndr.get(Calendar.HOUR_OF_DAY);
    int minute = calndr.get(Calendar.MINUTE);
    if (hour == 6 && minute >= 0 && minute <= 29) return 8;
    else if (hour == 6 && minute >= 30 && minute <= 59) return 13;
    else if (hour == 7 && minute >= 0 && minute <= 59) return 18;
    else if (hour == 8 && minute >= 0 && minute <= 29) return 13;
    // Is this suppose to be like this? No payment first half hour?
    else if (hour >= 8 && hour <= 14 && minute >= 30 && minute <= 59) return 8;
    else if (hour == 15 && minute >= 0 && minute <= 29) return 13;
    else if (hour == 15 && minute >= 0 || hour == 16 && minute <= 59) return 18;
    else if (hour == 17 && minute >= 0 && minute <= 59) return 13;
    else if (hour == 18 && minute >= 0 && minute <= 29) return 8;
    else return 0;
  }

  private Boolean isTollFreeDate(Date date) {
    // Check if day is on the weekend
    calndr.setTime(date);
    int dayOfWeek = calndr.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return true;
    // Check if day is currently a holiday
    /** Currently needs to be updated manually which is inoptimal.
    Would suggest that we fix this using an api or existing library
    https://github.com/svendiedrichsen/jollyday */
    int year = calndr.get(Calendar.YEAR);
    int month = calndr.get(Calendar.MONTH);
    int day = calndr.get(Calendar.DAY_OF_MONTH);
    if (year == 2020) {
      if (month == Calendar.JANUARY && day == 1 ||
          month == Calendar.APRIL && (day == 10 || day == 12 || day == 13) ||
          month == Calendar.MAY && (day == 1 || day == 21 || day == 31) ||
          month == Calendar.JUNE && (day == 6 || day == 20) ||
          month == Calendar.OCTOBER && day == 31 ||
          month == Calendar.DECEMBER && (day == 24 || day == 25 || day == 31)) {
        return true;
      }
    }
    return false;
  }

}
