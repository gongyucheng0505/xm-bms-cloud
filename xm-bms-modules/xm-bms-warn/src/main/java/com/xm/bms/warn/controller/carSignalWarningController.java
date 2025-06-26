package com.xm.bms.warn.controller;

import com.xm.bms.warn.domain.CarSignalWarning;
import com.xm.bms.warn.domain.WarnRequest;
import com.xm.bms.warn.domain.WarnResponse;
import com.xm.bms.warn.service.CarSignalWarningReluService;
import com.xm.bms.warn.service.CarSignalWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/api/carSignalWarnings")
public class carSignalWarningController {

    @Autowired
    private CarSignalWarningService carSignalWarningService;

    @PostMapping("/warn")
    public List<WarnResponse> reportWarning(@RequestBody List<WarnRequest> warnData) {
        return carSignalWarningService.processWarning(warnData);
    }


}
