package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.entity.Invoice;
import com.orbvpn.api.service.InvoiceService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.util.List;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;
import static com.orbvpn.api.domain.enums.RoleName.Constants.RESELLER;

@Component
@RequiredArgsConstructor
public class InvoiceQuery implements GraphQLQueryResolver {

    private final InvoiceService invoiceService;

    @RolesAllowed({ADMIN, RESELLER})
    public List<Invoice> allInvoices() {
        return invoiceService.getAll();
    }

    @RolesAllowed({ADMIN, RESELLER})
    public Invoice getInvoiceById(Integer id) {
        return invoiceService.getById(id);
    }

    @RolesAllowed({ADMIN, RESELLER})
    public Invoice getInvoiceByPaymentId(Integer paymentId) {
        return invoiceService.getById(paymentId);
    }

    public List<Invoice> getByDateRange(LocalDate beginDate, LocalDate endDate) {
        return null;
    }
}
