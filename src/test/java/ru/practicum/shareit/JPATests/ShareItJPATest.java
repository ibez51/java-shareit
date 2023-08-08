package ru.practicum.shareit.JPATests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ShareItJPATest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void testPersistItemId() {
        User user = new User().setName("User name").setEmail("email@user.f");
        entityManager.persist(user);

        Item item = new Item()
                .setName("Item name")
                .setDescription("Item description")
                .setAvailable(true)
                .setOwner(user);

        assertEquals(0, item.getId());
        entityManager.persist(item);
        Assertions.assertNotNull(item.getId());
    }

    @Test
    void testSearchItems() {
        User user1 = new User().setName("User name1").setEmail("email@user.f1");
        entityManager.persist(user1);

        User user2 = new User().setName("User name2").setEmail("email@user.f2");
        entityManager.persist(user2);

        Item item1 = new Item()
                .setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(true)
                .setOwner(user1);
        itemRepository.save(item1);

        Item item2 = new Item()
                .setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(true)
                .setOwner(user2);
        itemRepository.save(item2);

        Item item3 = new Item()
                .setName("Др1ель")
                .setDescription("Простая др1ель")
                .setAvailable(true)
                .setOwner(user2);
        itemRepository.save(item3);

        assertEquals(2, itemRepository.findItemsByAvailabilityAndNameOrDesc("дРеЛь", PageRequest.of(0, 10)).stream().count());
        assertEquals(3, itemRepository.findItemsByAvailabilityAndNameOrDesc("", PageRequest.of(0, 10)).stream().count());
        assertEquals(3, itemRepository.findItemsByAvailabilityAndNameOrDesc("дР", PageRequest.of(0, 10)).stream().count());
    }

    @Test
    public void testFindLastBooking() throws InterruptedException {
        User user1 = new User().setName("User name1").setEmail("email@user.f1");
        entityManager.persist(user1);

        User user2 = new User().setName("User name2").setEmail("email@user.f2");
        entityManager.persist(user2);

        Item item1 = new Item()
                .setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(true)
                .setOwner(user1);
        itemRepository.save(item1);

        Booking bookingPast = new Booking()
                .setStart(LocalDateTime.now().plusSeconds(1))
                .setEnd(LocalDateTime.now().plusSeconds(2))
                .setItem(item1)
                .setBooker(user2)
                .setStatus(BookingStatus.WAITING);
        bookingRepository.save(bookingPast);

        Thread.sleep(3000);

        LocalDateTime currenBookingFrom = LocalDateTime.now().plusSeconds(2);
        LocalDateTime currenBookingTo = LocalDateTime.now().plusDays(1);
        Booking bookingCurrent = new Booking()
                .setStart(currenBookingFrom)
                .setEnd(currenBookingTo)
                .setItem(item1)
                .setBooker(user2)
                .setStatus(BookingStatus.WAITING);
        bookingRepository.save(bookingCurrent);

        Thread.sleep(3000);

        Booking bookingFuture = new Booking()
                .setStart(LocalDateTime.now().plusDays(5))
                .setEnd(LocalDateTime.now().plusDays(6))
                .setItem(item1)
                .setBooker(user2)
                .setStatus(BookingStatus.WAITING);
        bookingRepository.save(bookingFuture);

        List<Booking> bookingList = bookingRepository.findLastBooking(Set.of(item1.getId()),
                Set.of(BookingStatus.REJECTED.name(), BookingStatus.CANCELED.name()),
                LocalDateTime.now());
        assertEquals(currenBookingFrom, bookingList.stream().findFirst().get().getStart());
        assertEquals(currenBookingTo, bookingList.stream().findFirst().get().getEnd());
    }

    @Test
    public void testFindNextBooking() throws InterruptedException {
        User user1 = new User().setName("User name1").setEmail("email@user.f1");
        entityManager.persist(user1);

        User user2 = new User().setName("User name2").setEmail("email@user.f2");
        entityManager.persist(user2);

        Item item1 = new Item()
                .setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(true)
                .setOwner(user1);
        itemRepository.save(item1);

        Booking bookingPast = new Booking()
                .setStart(LocalDateTime.now().plusSeconds(1))
                .setEnd(LocalDateTime.now().plusSeconds(2))
                .setItem(item1)
                .setBooker(user2)
                .setStatus(BookingStatus.WAITING);
        bookingRepository.save(bookingPast);

        Thread.sleep(3000);

        Booking bookingCurrent = new Booking()
                .setStart(LocalDateTime.now().plusSeconds(2))
                .setEnd(LocalDateTime.now().plusDays(1))
                .setItem(item1)
                .setBooker(user2)
                .setStatus(BookingStatus.WAITING);
        bookingRepository.save(bookingCurrent);

        Thread.sleep(3000);

        LocalDateTime futureBookingFrom = LocalDateTime.now().plusDays(5);
        LocalDateTime futureBookingTo = LocalDateTime.now().plusDays(6);
        Booking bookingFuture = new Booking()
                .setStart(futureBookingFrom)
                .setEnd(futureBookingTo)
                .setItem(item1)
                .setBooker(user2)
                .setStatus(BookingStatus.WAITING);
        bookingRepository.save(bookingFuture);

        List<Booking> bookingList = bookingRepository.findNextBooking(Set.of(item1.getId()),
                Set.of(BookingStatus.REJECTED.name(), BookingStatus.CANCELED.name()),
                LocalDateTime.now());
        assertEquals(futureBookingFrom, bookingList.stream().findFirst().get().getStart());
        assertEquals(futureBookingTo, bookingList.stream().findFirst().get().getEnd());
    }

    @Test
    public void testExistsApprovedBookingInPast() throws InterruptedException {
        User user1 = new User().setName("User name1").setEmail("email@user.f1");
        entityManager.persist(user1);

        User user2 = new User().setName("User name2").setEmail("email@user.f2");
        entityManager.persist(user2);

        Item item1 = new Item()
                .setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(true)
                .setOwner(user1);
        itemRepository.save(item1);

        Booking bookingPast = new Booking()
                .setStart(LocalDateTime.now().plusSeconds(1))
                .setEnd(LocalDateTime.now().plusSeconds(2))
                .setItem(item1)
                .setBooker(user2)
                .setStatus(BookingStatus.WAITING);
        bookingRepository.save(bookingPast);

        assertFalse(bookingRepository.existsApprovedBookingInPast(item1.getId(),
                user2.getId(),
                Set.of(BookingStatus.REJECTED.name(), BookingStatus.CANCELED.name()),
                LocalDateTime.now()));

        Thread.sleep(3000);

        bookingPast.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingPast);

        assertTrue(bookingRepository.existsApprovedBookingInPast(item1.getId(),
                user2.getId(),
                Set.of(BookingStatus.REJECTED.name(), BookingStatus.CANCELED.name()),
                LocalDateTime.now()));
    }
}