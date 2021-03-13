package ca.mcgill.ecse321.repairshopmanagementsystem.controller;

import ca.mcgill.ecse321.repairshopmanagementsystem.dto.*;
import ca.mcgill.ecse321.repairshopmanagementsystem.model.*;
import ca.mcgill.ecse321.repairshopmanagementsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/appointment")
public class AppointmentController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SystemService systemService;

    /*
    ----------------------------------------------------------------------------
    ------------------------------Appointment-----------------------------------
    ----------------------------------------------------------------------------
    */

    @PostMapping(value = "appointments/find_appointments_of_customer")
    public List<AppointmentDto> findAppointmentsOfACustomer(@RequestParam String username) {
        List<Appointment> aps = appointmentService.findAppointmentsOfCustomer(username);
        return convertToDtoListForAppointment(aps);
    }

    @GetMapping(value = "appointments")
    public List<AppointmentDto> getAllAppointments() {
        List<Appointment> aps = appointmentService.getAllAppointments();
        return convertToDtoListForAppointment(aps);
    }

    @PostMapping(value = "appointments/make_appointment")
    public AppointmentDto makeAppointment(@RequestParam String serviceType,
                                          @RequestParam String username,
                                          @RequestParam String plateNo,
                                          @RequestParam Date startDate,
                                          @RequestParam Time startTime,
                                          @RequestParam Time endTime,
                                          @RequestParam Integer weight) {
        Appointment aps = appointmentService.makeAppointment(serviceType, username, plateNo, startDate, startTime, endTime, weight);
        return convertToDto(aps);
    }

    @PostMapping(value = "appointments/update_service_type")
    public AppointmentDto updateServiceType(@RequestParam Appointment appointment, @RequestParam String newServiceType) {
        return convertToDto(appointmentService.changeServiceType(appointment, newServiceType));
    }

    @PostMapping(value = "appointments/delete")
    public AppointmentDto deleteAppointment(@RequestParam Integer id) {
        return convertToDto(appointmentService.deleteAppointment(appointmentService.getAppointmentById(id)));
    }

    @PostMapping(value = "appointments/update_appointment_time")
    public AppointmentDto UpdateAppointmentTime(@RequestParam Integer shiftId, @RequestParam Integer appointmentId) {
        Appointment aps = appointmentService.UpdateAppointmentTime(shiftId, appointmentId);
        return convertToDto(aps);
    }

    /*
    ----------------------------------------------------------------------------
    -----------------------------------Bill-------------------------------------
    ----------------------------------------------------------------------------
    */

    @PostMapping(value = "make_payment")
    public BillDto makePayment(@RequestParam Integer id) {
        Bill newBill = appointmentService.makePayment(appointmentService.makePayment(appointmentService.getBillByBillNo(id)));
        return convertToDto(newBill);
    }

    @GetMapping(value = "/bills")
    public List<BillDto> getAllBills() {
        List<Bill> billDtoList = appointmentService.getAllBills();
        return convertToDtoListForBill(billDtoList);
    }

    /*
    ----------------------------------------------------------------------------
    ------------------------------ Service -------------------------------------
    ----------------------------------------------------------------------------
     */

    @GetMapping(value = "services")
    public List<ServiceDto> getAllServices() {
        List<Service> service = appointmentService.getAllServices();
        return convertToDtoListForService(service);
    }

    /*
    ----------------------------------------------------------------------------
    -------------------------------- Space -------------------------------------
    ----------------------------------------------------------------------------
     */
    @PostMapping(value = "space/create")
    public SpaceDto createSpace(@RequestParam Integer weight) {
        return convertToDto(appointmentService.createSpace(weight, systemService.getMostRecentSystem()));
    }

    private List<ServiceDto> convertToDtoListForService(List<Service> service) {
        List<ServiceDto> result = new ArrayList<>();
        for (Service s : service) {
            result.add(convertToDto(s));
        }
        return result;
    }


    private AppointmentDto convertToDto(Appointment appointment) {
        List<Assistant> ass = new ArrayList<>();
        for (Assistant a : appointment.getService().getAssistant()) {
            ass.add(a);
        }
        // Integer appointmentId, Set<BillDto> bill, ServiceDto service, ShiftDto shift,
        // CustomerDto customer, CarDto car, SpaceDto space
        ServiceDto sto = new ServiceDto(appointment.getService().getServiceType(), convertToDtoListForAssistant(ass));

        List<BillDto> billresult = new ArrayList<>();
        for (Bill a : appointment.getBill()) {
            BillDto bill = new BillDto(a.getBillNo(), a.getPrice(), a.getIsPaid());
            billresult.add(bill);
        }
        return new AppointmentDto(appointment.getAppointmentId(), billresult, convertToDto(appointment.getShift()),
                convertToDto(appointment.getCustomer()), convertDtoListForCar(appointment.getCar()), convertToDto(appointment.getSpace()));
    }

    private List<AppointmentDto> convertToDtoListForAppointment(List<Appointment> appointments) {
        List<AppointmentDto> result = new ArrayList<>();
        for (Appointment a : appointments) {
            result.add(convertToDto(a));

        }
        return result;

    }

    private ShiftDto convertToDto(Shift shift) {
        ScheduleDto sto = new ScheduleDto(shift.getSchedule().getId(), convertToDto(shift.getSchedule().getRepairShopManagementSystem()));
        return new ShiftDto(shift.getDate(), shift.getStartTime(), shift.getEndTime(),
                convertToDto(shift.getAssistant()), convertToDto(shift.getAppointment()), sto, shift.getShiftId());
    }

    private List<ShiftDto> convertToDtoListForShift(Set<Shift> shifts) {
        List<ShiftDto> result = new ArrayList<>();
        for (Shift s : shifts) {
            result.add(convertToDto(s));
        }
        return result;
    }

    private ScheduleDto convertToDto(Schedule schedule) {

        return new ScheduleDto(schedule.getId(), convertToDtoListForShift(schedule.getTimeSlot()),
                convertToDto(schedule.getRepairShopManagementSystem()));
    }

    private ServiceDto convertToDto(Service service) {
        List<Assistant> ass = new ArrayList<>();
        for (Assistant a : service.getAssistant()) {
            ass.add(a);
        }
        return new ServiceDto(service.getServiceType(), convertToDtoListForAssistant(ass),
                convertToDto(service.getAppointment()));

    }

    private BillDto convertToDto(Bill bill) {

        return new BillDto(bill.getBillNo(), bill.getPrice(), convertToDto(bill.getAppointment()), bill.getIsPaid());
    }


    private SpaceDto convertToDto(Space space) {
        return new SpaceDto(space.getSpaceId(), space.getMaxWeightLoad(), convertToDto(space.getRepairShopManagementSystem()));
    }

    private List<BillDto> convertToDtoListForBill(List<Bill> billList) {
        List<BillDto> result = new ArrayList<>();
        for (Bill b : billList) {
            result.add(convertToDto(b));
        }

        return result;
    }


    private List<CarDto> convertDtoListForCar(Set<Car> cars) {
        List<CarDto> carList = new ArrayList<>();
        for (Car c : cars) {


            carList.add(convertToDto(c));
        }


        return carList;
    }

    // ----------------------------all the dto from account
    // service-----------------------------------
    private RepairShopManagementSystemDto convertToDto(RepairShopManagementSystem system) {
        return new RepairShopManagementSystemDto(system.getBusinessName(), system.getBusinessPhoneNumber(),
                system.getBusinessPhoneNumber());
    }


    private List<OwnerDto> convertToDtoListForOwner(List<Owner> ownerList) {
        List<OwnerDto> result = new ArrayList<>();
        for (Owner o : ownerList)
            result.add(convertToDto(o));
        return result;
    }

    private List<AssistantDto> convertToDtoListForAssistant(List<Assistant> user) {
        List<AssistantDto> result = new ArrayList<>();
        for (Assistant o : user)
            result.add(convertToDto(o));
        return result;
    }

    private List<CustomerDto> convertToDtoListForCustomer(List<Customer> user) {
        List<CustomerDto> result = new ArrayList<>();
        for (Customer o : user)
            result.add(convertToDto(o));
        return result;
    }

    private OwnerDto convertToDto(Owner owner) {
        return new OwnerDto(owner.getUsername(), owner.getName(), owner.getPassword(),
                convertToDto(owner.getRepairShopManagementSystem()));
    }

    private AssistantDto convertToDto(Assistant a) {
        return new AssistantDto(a.getUsername(), a.getName(), a.getPassword(),
                convertToDto(a.getRepairShopManagementSystem()));
    }

    private CustomerDto convertToDto(Customer a) {
        return new CustomerDto(convertToDto(a.getRepairShopManagementSystem()), a.getUsername(), a.getPassword(),
                a.getName(), a.getPhoneNo(), a.getHomeAddress(), a.getEmail());
    }

    private CarDto convertToDto(Car c) {
        return new CarDto(c.getPlateNo(), c.getModel(), c.getManufacturer(), c.getYear());
    }

    private List<ShiftDto> convertToDtoListForShiftFromList(List<Shift> shifts) {
        List<ShiftDto> result = new ArrayList<>();
        for (Shift s : shifts) {
            result.add(convertToDto(s));
        }
        return result;
    }

}