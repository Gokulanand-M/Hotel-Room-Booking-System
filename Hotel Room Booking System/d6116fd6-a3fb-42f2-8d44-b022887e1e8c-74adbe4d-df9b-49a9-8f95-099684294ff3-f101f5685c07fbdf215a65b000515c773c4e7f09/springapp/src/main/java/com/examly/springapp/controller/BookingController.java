package com.examly.springapp.controller;

import com.examly.springapp.model.Booking;
import com.examly.springapp.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestBody Map<String, Object> payload) {
        Long roomId = Long.valueOf(payload.get("roomId").toString());
        Booking booking = new Booking();
        booking.setGuestName(payload.get("guestName").toString());
        booking.setGuestEmail(payload.get("guestEmail").toString());
        booking.setCheckInDate(java.time.LocalDate.parse(payload.get("checkInDate").toString()));
        booking.setCheckOutDate(java.time.LocalDate.parse(payload.get("checkOutDate").toString()));
        return bookingService.createBooking(booking, roomId);
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PutMapping("/{id}/status")
    public Booking updateBookingStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return bookingService.updateBookingStatus(id, status);
    }
}
