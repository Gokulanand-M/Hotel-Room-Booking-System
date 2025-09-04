package com.examly.springapp.service;

import com.examly.springapp.exception.BadRequestException;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Booking;
import com.examly.springapp.model.Room;
import com.examly.springapp.repository.BookingRepository;
import com.examly.springapp.repository.RoomRepository;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public Booking createBooking(Booking booking, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        if (!room.getAvailable()) {
            throw new BadRequestException("Room is not available");
        }

        if (!booking.getGuestEmail().contains("@")) {
            throw new BadRequestException("Invalid email format");
        }

        if (!booking.getCheckOutDate().isAfter(booking.getCheckInDate())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }

        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        booking.setTotalPrice(room.getPricePerNight() * days);
        booking.setRoom(room);
        booking.setStatus("PENDING");

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    public Booking updateBookingStatus(Long bookingId, String status) {
Booking booking = getBookingById(bookingId);
Room room = booking.getRoom();

if (!status.equals("APPROVED") && !status.equals("REJECTED")) {
throw new BadRequestException("Invalid status");
}

booking.setStatus(status);
if (status.equals("APPROVED")) {
room.setAvailable(false);
} else if (status.equals("REJECTED")) {
room.setAvailable(true);
}
roomRepository.save(room);

return bookingRepository.save(booking);
}
}