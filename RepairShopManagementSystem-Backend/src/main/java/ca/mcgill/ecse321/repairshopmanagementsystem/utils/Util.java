package ca.mcgill.ecse321.repairshopmanagementsystem.utils;

import java.sql.Time;
import java.util.regex.*;
import java.util.*;

import ca.mcgill.ecse321.repairshopmanagementsystem.model.*;

public class Util {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("(^[a-zA-Z0-9]+)@([a-zA-Z0-9]+)[.]([a-zA-Z]+)");

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{8,16}");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9]{8,16}");

    private static final Pattern PLATE_NO_PATTERN = Pattern.compile("^[A-Z][0-9]{6}");

    private static final Pattern PHONE_NO_PATTERN = Pattern.compile("^\\d{10}");

    /**
     * Returns whether an email address is valid.
     *
     * @param emailAddress An email Address.
     * @return True for valid, false otherwise.
     * @author Ing Tian
     */
    public static boolean isEmailAddressCorrect(String emailAddress) {
        Matcher matcher = EMAIL_PATTERN.matcher(emailAddress);
        return matcher.matches();
    }

    /**
     * Returns whether the username is valid.
     *
     * @param username A username
     * @return True for valid, false otherwise.
     * @author Ing Tian
     */
    public static boolean isUsernameCorrect(String username) {
        Matcher matcher = USERNAME_PATTERN.matcher(username);
        return matcher.matches();
    }

    /**
     * Returns whether the password is valid.
     *
     * @param password A password.
     * @return True for valid, false otherwise.
     * @author Ing Tian
     */
    public static boolean isPasswordCorrect(String password) {
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    /**
     * Returns whether the plate no is valid.
     *
     * @param plateNo A plate no.
     * @return True for valid, false otherwise.
     * @author Ing Tian
     */
    public static boolean isPlateNoCorrect(String plateNo) {
        Matcher matcher = PLATE_NO_PATTERN.matcher(plateNo);
        return matcher.matches();
    }

    /**
     * Returns whether the phone no is valid.
     *
     * @param phoneNo A phone no.
     * @return True for valid, false otherwise.
     * @author Ing Tian
     */
    public static boolean isPhoneNoCorrect(String phoneNo) {
        Matcher matcher = PHONE_NO_PATTERN.matcher(phoneNo);
        return matcher.matches();
    }

    /**
     * Returns whether two time intervals have overlaps.
     *
     * @param t1 First interval start time.
     * @param t2 First interval end time.
     * @param p1 Second interval start time.
     * @param p2 Second interval end time.
     * @return True for overlaps exist, false otherwise.
     * @author Ing Tian
     */
    public static boolean isIntervalOverlapped(Time t1, Time t2, Time p1, Time p2) {
        return (t1.after(p1) && t1.before(p2)) || (t1.before(p1) && t2.after(p1));
    }

    /**
     * Returns whether a new shift has conflicts with existing ones.
     *
     * @param assistantShifts An assigned array of shifts.
     * @param aNewShift       A new shift.
     * @return True for conflicts exist, false otherwise.
     * @author Ing Tian
     */
    public static boolean hasShiftConflicts(Set<Shift> assistantShifts, Shift aNewShift) {
        Time t1 = aNewShift.getStartTime(), t2 = aNewShift.getEndTime();
        for (Shift s : assistantShifts)
            if (aNewShift.getDate().equals(s.getDate()) && isIntervalOverlapped(t1, t2, s.getStartTime(), s.getEndTime()))
                return true;
        return false;
    }

    /**
     * Returns whether a shift can cover the appointment.
     *
     * @param s  A shift.
     * @param t1 Appointment start time.
     * @param t2 Appointment end time.
     * @return True for is contained, false otherwise.
     * @author Ing Tian
     */
    public static boolean isAppointmentTimeCoveredByShift(Shift s, Time t1, Time t2) {
        return s.getStartTime().before(t1) && s.getEndTime().after(t2);
    }

    /**
     * Compute whether an appointment can be made at that moment.
     *
     * @param loadRequirement      The load requirement for the car, in kg.
     * @param appointmentDate      The appointment date.
     * @param appointmentStartTime The appointment start time.
     * @param appointmentEndTime   The appointment end time.
     * @param weekSchedule         The schedule of shifts on the week when the appointment takes place.
     * @param spaceSet             The set of all space.
     * @param freeShifts           Available shifts that cover the appointment.
     * @param freeSpace            Available space when the appointment takes place.
     * @return True for possible, false otherwise.
     */
    public static boolean canMakeAppointment(int loadRequirement, Date appointmentDate, Time appointmentStartTime, Time appointmentEndTime, Schedule weekSchedule, Set<Space> spaceSet, List<Space> freeSpace, List<Shift> freeShifts) {
        List<Space> occupiedSpaceAtAppointmentTime = new ArrayList<>();

        // Count all occupied space and available shifts.
        for (Shift s : weekSchedule.getTimeSlot())
            if (s.getDate().equals(appointmentDate) && s.getAppointment() == null && isAppointmentTimeCoveredByShift(s, appointmentStartTime, appointmentEndTime))
                freeShifts.add(s);
            else if (s.getDate().equals(appointmentDate) && s.getAppointment() != null && isIntervalOverlapped(s.getStartTime(), s.getEndTime(), appointmentStartTime, appointmentEndTime))
                occupiedSpaceAtAppointmentTime.add(s.getAppointment().getSpace());

        // Get free space that meet the load requirement.
        for (Space s : spaceSet)
            if (!occupiedSpaceAtAppointmentTime.contains(s) && s.getMaxWeightLoad() >= loadRequirement)
                freeSpace.add(s);

        return freeSpace.size() != 0 && freeShifts.size() != 0;
    }

}
