package org.example.council;

import org.example.authService.PermitIssuerService;
import org.example.authService.VerificationService;
import org.example.exception.OwnerNotRegisteredException;
import org.example.exception.PermitIssueFailedException;
import org.example.owner.Owner;
import org.example.vehicle.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TownCouncilTest {
TownCouncil underTest;
Owner owner1;
Owner owner2;

@Mock
VerificationService mockVerificationService;

@Mock
PermitIssuerService mockPermitIssuerService;

@BeforeEach
    void setup() {
    underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);
    owner1 = new Owner("Jake","234sdfasdfss");
    owner2 = new Owner("Mike","sdfd8777989");

}

    @Test
    void testThrowOwnerNotRegisteredException() {
        // given
        PrivateVehicle pv1 = new PrivateVehicle("GH-481-2",owner1, VehicleType.PRIVATE);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(false);
        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);
        // then
        assertThrows(OwnerNotRegisteredException.class,()-> underTest.issuePermit(pv1,owner1));
    }
    
    @Test
    void testThrowPermitIssueFailedException() {
        // given
        PrivateVehicle pv1 = new PrivateVehicle("GH-481-2",owner1, VehicleType.PRIVATE);

        //when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        // then
        assertThrows(PermitIssueFailedException.class,()-> underTest.issuePermit(pv1,owner1));
    }

    @Test
    void testDoesNotThrowException() {
        // given
        PrivateVehicle pv1 = new PrivateVehicle("GH-481-2",owner1, VehicleType.PRIVATE);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("anything");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);
        // then
        assertDoesNotThrow(()->underTest.issuePermit(pv1,owner1));
    }

    @Test
    void testPermitsIssuedIncreases() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        PrivateVehicle pv1 = new PrivateVehicle("GH-481-2",owner1, VehicleType.PRIVATE);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("anything");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        underTest.issuePermit(pv1,owner1);
        // then
        assertEquals(1,underTest.getPermitsIssued().size());
    }

    @Test
    void testVehicleListUpdated() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        PrivateVehicle pv1 = new PrivateVehicle("GH-481-2",owner1, VehicleType.PRIVATE);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("anything");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        underTest.issuePermit(pv1,owner1);
        // then
        assertEquals(1,underTest.getVehicles().size());
    }

    @Test
    void testVehicleTypeListIncreaseToTwo() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        PrivateVehicle pv1 = new PrivateVehicle("GH-481-2",owner1, VehicleType.PRIVATE);
        PrivateVehicle pv2 = new PrivateVehicle("GH-2281-2",owner2, VehicleType.PRIVATE);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("anything");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        underTest.issuePermit(pv1,owner1);
        underTest.issuePermit(pv2,owner2);
        var privateTypeList = underTest.getVehicles().get(VehicleType.PRIVATE);
        // then
        assertEquals(2,privateTypeList.size());
    }

    @Test
    void testIfVehicleListContainsTwoTypesOfVehicle() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        PrivateVehicle pv1 = new PrivateVehicle("GH-481-2",owner1, VehicleType.PRIVATE);
        MotorBike mv1 = new MotorBike("GH-8080-3",owner2,VehicleType.MOTOR,850);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("anything");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        underTest.issuePermit(pv1,owner1);
        underTest.issuePermit(mv1,owner2);
        // then
        assertEquals(2,underTest.getVehicles().size());
    }

    @Test
    void testPrivateVehicleReturnsDefaultCharge() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        PrivateVehicle pv1 = new PrivateVehicle("GH-481-2",owner1, VehicleType.PRIVATE);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("anything");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        var permit = underTest.issuePermit(pv1,owner1);
        // then
        assertEquals(20.0,permit.getCharge());
    }

    @Test
    void testMotorBikeReturnsDefaultCharge() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        MotorBike mv1 = new MotorBike("GH-8080-3",owner2,VehicleType.MOTOR,350);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("anything");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        var permit = underTest.issuePermit(mv1,owner2);
        // then
        assertEquals(7.0,permit.getCharge());
    }

    @Test
    void testMotorBikeReturnsExtraCharge() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        MotorBike mv1 = new MotorBike("GH-8080-3",owner2,VehicleType.MOTOR,900);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("anything");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        var permit = underTest.issuePermit(mv1,owner2);
        // then
        assertEquals(10.0,permit.getCharge());
    }

    @Test
    void testSiteVehicleReturnsDefaultCharge() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        SiteVehicle sv1 = new SiteVehicle("GH-8080-3",owner2,VehicleType.TRACK,150);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        var permit = underTest.issuePermit(sv1,owner2);
        // then
        assertEquals(30.0,permit.getCharge());
    }

    @Test
    void testSiteVehicleReturnsChargePlusExtraCharge() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        SiteVehicle sv1 = new SiteVehicle("GH-8080-3",owner2,VehicleType.TRACK,200);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        var permit = underTest.issuePermit(sv1,owner2);
        // then
        assertEquals(45.0,permit.getCharge());
    }

    @Test
    void testShouldReturnListOfVehiclesOfSameType() throws OwnerNotRegisteredException, PermitIssueFailedException {
        // given
        PrivateVehicle pv1 = new PrivateVehicle("GH-481-2",owner1, VehicleType.PRIVATE);
        PrivateVehicle pv2 = new PrivateVehicle("GH-2281-2",owner2, VehicleType.PRIVATE);

        // when
        when(mockVerificationService.verifyPerson(any(Owner.class),any(Vehicle.class))).thenReturn(true);
        when(mockPermitIssuerService.issuePermit(any(Vehicle.class))).thenReturn("anything");

        underTest = new TownCouncil(mockVerificationService, mockPermitIssuerService);

        underTest.issuePermit(pv1,owner1);
        underTest.issuePermit(pv2,owner2);

        var vehicleList = underTest.getVehiclesByType(VehicleType.PRIVATE);
        // then
        assertEquals(2,vehicleList.size());
    }

}