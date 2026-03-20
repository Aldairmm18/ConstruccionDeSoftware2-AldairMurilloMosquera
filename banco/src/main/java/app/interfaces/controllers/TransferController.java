package app.interfaces.controllers;

import app.application.services.TransferService;
import app.domain.models.Transfer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

  private final TransferService transferService;

  @PostMapping
  public Transfer create(@RequestBody Transfer transfer) {
    return transferService.create(transfer);
  }

  @GetMapping("/{id}")
  public Transfer findById(@PathVariable Long id) {
    return transferService.findById(id);
  }

  @GetMapping
  public List<Transfer> findAll() {
    return transferService.findAll();
  }
}
