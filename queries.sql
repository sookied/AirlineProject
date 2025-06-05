
ALTER TABLE FlightInstance ADD COLUMN IF NOT EXISTS DepartureTime TIMESTAMP;
ALTER TABLE FlightInstance ADD COLUMN IF NOT EXISTS ArrivalTime TIMESTAMP;
ALTER TABLE FlightInstance ADD COLUMN IF NOT EXISTS TicketCost NUMERIC;


INSERT INTO Plane (PlaneID, Make, Model, Year)
VALUES ('P123', 'Boeing', '737', 2015)
ON CONFLICT (PlaneID) DO NOTHING;


INSERT INTO Customer (CustomerID, FirstName, LastName, Gender, DOB, Address, Phone, Zip)
VALUES (10, 'John', 'Doe', 'M', '1990-01-01', '123 Main St', '555-1234', '12345')
ON CONFLICT (CustomerID) DO NOTHING; 


INSERT INTO Technician (TechnicianID, Name)
VALUES ('T101', 'John Technician')
ON CONFLICT (TechnicianID) DO NOTHING;

-- Insert missing pilot 'P321' if it doesn't exist
INSERT INTO Pilot (PilotID, Name)
VALUES ('P321', 'Jane Pilot')
ON CONFLICT (PilotID) DO NOTHING;


SELECT SeatsTotal - SeatsSold AS SeatsAvailable
FROM FlightInstance
WHERE FlightInstanceID = 42;

-- Insert reservation with new ID
INSERT INTO Reservation (ReservationID, CustomerID, FlightInstanceID, Status)
VALUES ('R1000', 10, 42, 'reserved')
ON CONFLICT (ReservationID) DO NOTHING;


SELECT DayOfWeek, DepartureTime, ArrivalTime
FROM Schedule
WHERE FlightNumber = 'XYZ123';

SELECT SeatsTotal - SeatsSold AS SeatsAvailable, SeatsSold
FROM FlightInstance
WHERE FlightNumber = 'XYZ123' AND FlightDate = '2025-06-01';

SELECT DepartedOnTime, ArrivedOnTime
FROM FlightInstance
WHERE FlightNumber = 'XYZ123' AND FlightDate = '2025-06-01';

SELECT FlightNumber, FlightDate, DepartureCity, ArrivalCity
FROM FlightInstance
JOIN Flight USING (FlightNumber)
WHERE FlightDate = '2025-06-01';

SELECT C.*
FROM Reservation R
JOIN Customer C ON R.CustomerID = C.CustomerID
JOIN FlightInstance FI ON R.FlightInstanceID = FI.FlightInstanceID
WHERE FI.FlightNumber = 'XYZ123'
  AND FI.FlightDate = '2025-06-01'
  AND R.Status = 'reserved';

SELECT C.FirstName, C.LastName, C.Gender, C.DOB, C.Address, C.Phone, C.Zip
FROM Reservation R
JOIN Customer C ON R.CustomerID = C.CustomerID
WHERE R.ReservationID = 'R1000';

SELECT Make, Model, (EXTRACT(YEAR FROM CURRENT_DATE) - Year) AS Age, LastRepairDate
FROM Plane
WHERE PlaneID = 'P123';

SELECT *
FROM Repair
WHERE TechnicianID = 'T456';

SELECT RepairDate, RepairCode
FROM Repair
WHERE PlaneID = 'P123'
  AND RepairDate BETWEEN '2025-05-01' AND '2025-06-01';

SELECT COUNT(*) AS TotalDays,
       SUM(CASE WHEN DepartedOnTime THEN 1 ELSE 0 END) AS DepartedOnTimeCount,
       SUM(CASE WHEN ArrivedOnTime THEN 1 ELSE 0 END) AS ArrivedOnTimeCount,
       SUM(SeatsSold) AS TotalTicketsSold,
       SUM(SeatsTotal - SeatsSold) AS TotalTicketsUnsold
FROM FlightInstance
WHERE FlightNumber = 'XYZ123'
  AND FlightDate BETWEEN '2025-05-01' AND '2025-06-01';

SELECT FI.FlightNumber, FI.DepartureTime, FI.ArrivalTime, FI.NumOfStops,
       ROUND(100.0 * SUM(CASE WHEN DepartedOnTime THEN 1 ELSE 0 END) / COUNT(*), 2) AS OnTimePct
FROM FlightInstance FI
JOIN Flight F ON FI.FlightNumber = F.FlightNumber
WHERE F.DepartureCity = 'LAX' AND F.ArrivalCity = 'JFK' AND FI.FlightDate = '2025-06-01'
GROUP BY FI.FlightNumber, FI.DepartureTime, FI.ArrivalTime, FI.NumOfStops;

SELECT TicketCost
FROM FlightInstance
WHERE FlightNumber = 'XYZ123' AND FlightDate = '2025-06-01';

SELECT P.Make, P.Model
FROM Flight F
JOIN Plane P ON F.PlaneID = P.PlaneID
WHERE F.FlightNumber = 'XYZ123';


SELECT *
FROM MaintenanceRequest
WHERE PilotID = 'P789';

INSERT INTO Repair (RepairID, PlaneID, RepairCode, RepairDate, TechnicianID)
VALUES (123, 'P123', 'RC456', '2025-06-02', 'T101')
ON CONFLICT (RepairID) DO NOTHING;

INSERT INTO MaintenanceRequest (RequestID, PlaneID, RepairCode, RequestDate, PilotID)
VALUES (456, 'P123', 'RC789', '2025-06-03', 'P321')
ON CONFLICT (RequestID) DO NOTHING;
