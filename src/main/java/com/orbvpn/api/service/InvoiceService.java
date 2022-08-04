package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.InvoiceUpdate;
import com.orbvpn.api.domain.entity.Invoice;
import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserProfile;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.reposiitory.InvoiceRepository;
import com.orbvpn.api.reposiitory.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserProfileRepository userProfileRepository;

    public void createInvoice(Payment payment) {
        User user = payment.getUser();
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(null);
        if ( profile == null )
            throw new NotFoundException(String.format("User Profile could not found for the user %d.", user.getId()));

        Invoice invoice = Invoice.builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .address(profile.getAddress())
                .phoneNumber(profile.getPhone())
                .email(user.getEmail())
                .paymentDate(payment.getCreatedAt())
                .paymentMethod(payment.getGateway())
                .totalAmount(payment.getPrice())
                .build();

        if (payment.getCategory().equals(PaymentCategory.GROUP)) {
            invoice.setAmountForGroup(payment.getPrice());
            invoice.setGroupId(payment.getGroupId());
        } else  if (payment.getCategory().equals(PaymentCategory.MORE_LOGIN)) {
            invoice.setAmountForMultiLogin(payment.getPrice());
            invoice.setMultiLogin(payment.getMoreLoginCount());
        }

        invoiceRepository.save(invoice);
    }

    public List<Invoice> getAll() {
        return invoiceRepository.findAll();
    }

    public Invoice getById(Integer id) {
        return invoiceRepository.getById(id);
    }

    public Invoice getByPaymentId(int paymentId) {
        return invoiceRepository.findByPaymentId(paymentId)
                .orElse(null);
    }

    public Invoice updateInvoice(InvoiceUpdate invoiceUpdate) {
        Invoice invoice = invoiceRepository.getById(invoiceUpdate.getId());
        invoice = invoiceUpdate.updateInvoice(invoice);
        invoiceRepository.save(invoice);
        return invoice;
    }

    public List<Invoice> getByDateRange(LocalDate beginDate, LocalDate endDate) {
        return invoiceRepository.getByDateRange(beginDate,endDate);
    }
}
