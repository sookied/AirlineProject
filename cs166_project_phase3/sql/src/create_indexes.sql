-- Flight-related indexes
--Columns in WHERE, JOIN, or ORDER BY clauses
--speeds up the joins between the flight and plane tbles and route based search 
CREATE INDEX idx_flight_plane ON Flight(PlaneID);
CREATE INDEX idx_flight_route ON Flight(DepartureCity, ArrivalCity);

-- Schedule indexes
--faster schedule lookups and optimizes the quereies that use the day of the week and the time
CREATE INDEX idx_schedule_flight ON Schedule(FlightNumber);
CREATE INDEX idx_schedule_day_time ON Schedule(DayOfWeek, DepartureTime);

-- FlightInstance indexes (most critical for performance)
--quickly finds delayed flights and arrivals and seat availibiliity
CREATE INDEX idx_flightinstance_flightnumber_date ON FlightInstance(FlightNumber, FlightDate);
CREATE INDEX idx_flightinstance_departed ON FlightInstance(DepartedOnTime) WHERE DepartedOnTime = FALSE;
CREATE INDEX idx_flightinstance_arrived ON FlightInstance(ArrivedOnTime) WHERE ArrivedOnTime = FALSE;
CREATE INDEX idx_flightinstance_seats ON FlightInstance(SeatsSold, SeatsTotal);

-- Customer indexes 
CREATE INDEX idx_customer_name ON Customer(LastName, FirstName);
CREATE INDEX idx_customer_contact ON Customer(Phone, Email);

-- Reservation indexes
CREATE INDEX idx_reservation_customer ON Reservation(CustomerID);
CREATE INDEX idx_reservation_flightinstance ON Reservation(FlightInstanceID);
CREATE INDEX idx_reservation_status ON Reservation(Status) WHERE Status != 'flown';

-- Maintenance indexes
CREATE INDEX idx_repair_plane ON Repair(PlaneID);
CREATE INDEX idx_repair_technician ON Repair(TechnicianID);
CREATE INDEX idx_repair_date ON Repair(RepairDate);
CREATE INDEX idx_maintenancerequest_plane ON MaintenanceRequest(PlaneID);
CREATE INDEX idx_maintenancerequest_pilot ON MaintenanceRequest(PilotID);
CREATE INDEX idx_maintenancerequest_date ON MaintenanceRequest(RequestDate);

-- Composite indexes for common join operations
CREATE INDEX idx_flightinstance_composite ON FlightInstance(FlightNumber, FlightDate, SeatsSold);
CREATE INDEX idx_reservation_composite ON Reservation(CustomerID, FlightInstanceID, Status);