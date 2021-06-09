package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.PaymentType;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.reposiitory.GroupRepository;
import com.orbvpn.api.reposiitory.ResellerRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UploadUserService {

  private static final Map<String, Integer> resellerMap = Map.of("OrbVPN", 1,
    "Hosseing Aghanassir", 2, "Ali Sadeghi", 3);
  private static final Map<String, Integer> groupMap = Map
    .of("Iran Service", 2, "Agent Service", 1);

  private final UserRepository userRepository;
  private final ResellerRepository resellerRepository;
  private final GroupRepository groupRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserSubscriptionService userSubscriptionService;
  private final RoleService roleService;

  public boolean uploadUsers(InputStream inputStream) {

    try {
      Workbook workbook = new XSSFWorkbook(inputStream);
      Sheet datatypeSheet = workbook.getSheetAt(0);
      Iterator<Row> iterator = datatypeSheet.iterator();

      log.info("Number of rows: {}", datatypeSheet.getPhysicalNumberOfRows());

      // Ignore column names
      iterator.next();
      while (iterator.hasNext()) {

        Row currentRow = iterator.next();

        Cell usernameCell = currentRow.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String username = usernameCell.getStringCellValue();

        if (StringUtils.isBlank(username)) {
          continue;
        }

        Cell passwordCell = currentRow.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String password;
        if (passwordCell.getCellType() == CellType.NUMERIC) {
          password = NumberToTextConverter.toText(passwordCell.getNumericCellValue());
        } else {
          password = passwordCell.getStringCellValue();
        }

        Cell resellerCell = currentRow.getCell(4, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String reseller = resellerCell.getStringCellValue();

        Cell servicePackageCell = currentRow.getCell(5, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String servicePackage = servicePackageCell.getStringCellValue();

        User user = new User();

        user.setUsername(username);
        if (isValidEmail(username)) {
          user.setEmail(username);
        } else {
          user.setEmail("invalid@mail.com");
        }
        user.setFirstName("Migrated");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode(password));
        user.setRadAccess(UUID.randomUUID().toString());
        Role role = roleService.getByName(RoleName.USER);
        user.setRole(role);

        // Set resellers
        Integer resellerId = resellerMap.getOrDefault(reseller, 1);
        Reseller resellerEnt = resellerRepository.getOne(resellerId);
        user.setReseller(resellerEnt);

        userRepository.save(user);
        // Create subscription
        Integer groupId = groupMap.getOrDefault(servicePackage, 1);
        Group group = groupRepository.getOne(groupId);
        String paymentId = UUID.randomUUID().toString();
        UserSubscription userSubscription = userSubscriptionService
          .createUserSubscription(user, group, PaymentType.RESELLER_CREDIT, PaymentStatus.PENDING,
            paymentId);
        userSubscriptionService.fullFillSubscription(userSubscription);
      }
    } catch (IOException ioException) {
      throw new RuntimeException("Can not upload users");
    }

    return true;
  }

  private boolean isValidEmail(String email) {
    Pattern pattern = Pattern.compile("^.+@.+\\..+$");
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

}
