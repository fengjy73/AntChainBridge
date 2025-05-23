pragma solidity ^0.4.16;

/**
 * @title TypesToBytes
 * @dev The TypesToBytes contract converts the standard solidity types to the byte array
 */

library TypesToBytes {
 
    function identityToBytes(uint _offst, identity _input, bytes memory _output) internal pure {

        assembly {
            mstore(add(_output, _offst), _input)
        }
    }

    function bytes32ToBytes(uint _offst, bytes32 _input, bytes memory _output) internal pure {

        assembly {
            mstore(add(_output, _offst), _input)
            // mstore(add(add(_output, _offst),32), add(_input,32))
        }
    }
    
    function boolToBytes(uint _offst, bool _input, bytes memory _output) internal pure {
        uint8 x = _input == false ? 0 : 1;
        assembly {
            mstore(add(_output, _offst), x)
        }
    }
    
    function stringToBytes(uint _offst, bytes memory _input, bytes memory _output) internal {
        uint256 stack_size = _input.length / 32;
        if(_input.length % 32 > 0) stack_size++;
        
        assembly {
            let index := 0
            stack_size := add(stack_size,1)//adding because of 32 first bytes memory as the length
        loop:
            
            mstore(add(_output, _offst), mload(add(_input,mul(index,32))))
            _offst := sub(_offst , 32)
            index := add(index ,1)
            jumpi(loop , lt(index,stack_size))
        }
    }

    function intToBytes(uint _offst, int _input, bytes memory  _output) internal pure {

        assembly {
            mstore(add(_output, _offst), _input)
        }
    } 
    
    function uintToBytes(uint _offst, uint _input, bytes memory _output) internal pure {

        assembly {
            mstore(add(_output, _offst), _input)
        }
    }

}
